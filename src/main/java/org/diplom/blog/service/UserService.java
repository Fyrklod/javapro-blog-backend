package org.diplom.blog.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.diplom.blog.api.request.ProfileRequest;
import org.diplom.blog.api.request.RecoveryRequest;
import org.diplom.blog.api.response.ProfileResponse;
import org.diplom.blog.api.response.SimpleResponse;
import org.diplom.blog.dto.ImageType;
import org.diplom.blog.dto.ProfileError;
import org.diplom.blog.exception.AuthException;
import org.diplom.blog.api.request.AuthRequest;
import org.diplom.blog.api.request.UserRequest;
import org.diplom.blog.api.response.AuthResponse;
import org.diplom.blog.api.response.UserResponse;
import org.diplom.blog.dto.AuthError;
import org.diplom.blog.dto.mapper.UserMapper;
import org.diplom.blog.model.ModerationStatus;
import org.diplom.blog.model.User;
import org.diplom.blog.repository.PostRepository;
import org.diplom.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    @Value("${blog.info.title}")
    private String siteTitle;

    @Value("${blog.url}")
    private String blogUrl;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CaptchaService captchaService;
    private final ImageService imageService;
    private final SettingService settingService;
    private final MailSenderService mailSenderService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       CaptchaService captchaService,
                       ImageService imageService,
                       PostRepository postRepository,
                       MailSenderService mailSenderService,
                       @Lazy SettingService settingService,
                       @Lazy AuthenticationManager authenticationManager){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.captchaService = captchaService;
        this.imageService = imageService;
        this.settingService = settingService;
        this.postRepository = postRepository;
        this.mailSenderService = mailSenderService;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<UserResponse> login(UserRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            User currentUser = (User)auth.getPrincipal();
            return ResponseEntity.ok(prepareUserResponse(currentUser));

        } catch (AuthenticationException authEx) {
            log.error("Пользователю {} не удалось авторизоваться по причине ошибки {}", request.getEmail()
                                                                                      , authEx.getMessage());
            return ResponseEntity.ok(new UserResponse());
        }
    }

    @SneakyThrows
    public ResponseEntity<SimpleResponse> logout(HttpServletRequest httpRequest,
                                                 HttpServletResponse httpResponse) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
        //httpResponse.sendRedirect("/");

        return ResponseEntity.ok(new SimpleResponse(true));
    }

    public ResponseEntity<UserResponse> check() {
        try{
            User currentUser = getCurrentUser();
            return ResponseEntity.ok(prepareUserResponse(currentUser));
        } catch (AccessDeniedException accessEx) {
            return ResponseEntity.ok(new UserResponse());
        }
    }

    public ResponseEntity<SimpleResponse> restore(RecoveryRequest request) {
        boolean result;

        try {
            String restoreHash = generateRestoreHash();
            User user = getUserByEmail(request.getEmail());
            user.setCode(restoreHash);
            userRepository.save(user);

            String urlForRestore = String.format("%s/login/change-password/%s",
                    blogUrl, UUID.randomUUID());

            String letterText = String.format("Добрый день, %s\n" +
                    "\n" +
                    "Вы запросили восстановление пароля на нашем сайте. Для продолжения пройдите по адресу:\n" +
                    "<a href=\"%s\">%s</a>\n" +
                    "С уважением,\n" +
                    "Команда \"%s\"", user.getFullName(), urlForRestore, urlForRestore, siteTitle);
            //CompletableFuture.runAsync(() -> mailSenderService.send(user.getEmail(), "Восстановление пароля",
            //        letterText));
            mailSenderService.send(user.getEmail(), "Восстановление пароля", letterText);

            result = true;
        } catch(Exception ex) {
            log.info("User {} not found for restore", request.getEmail());
            result = false;
        }

        return ResponseEntity.ok(new SimpleResponse(result));
    }

    @Transactional
    public ResponseEntity<AuthResponse> password(AuthRequest request) {
        AuthResponse response = new AuthResponse(true);

        try {
            if(request.getPassword().length() < 6) {
                log.error("При восстановлении пароля для пользователя {} введен некорректный пароль", request.getEmail());

                throw new AuthException(AuthError.builder()
                        .password("Пароль короче 6-ти символов")
                        .build()
                );
            }

            if(captchaService.checkCaptchaCode(request.getCaptcha(), request.getCaptchaSecret())) {
                User user = userRepository.findByCode(request.getCode())
                        .orElseThrow(() -> new AuthException(AuthError.builder()
                                .code("Ссылка для восстановления пароля устарела. " +
                                        "<a href=\"/login/restore-password\">Запросить ссылку снова</a>")
                                .build()
                        ));


                user.setCode(null);
                user.setPassword(passwordEncoder.encode(request.getPassword()));

                userRepository.save(user);

                log.info("Пароль для пользователя {} успешно восстановлен", user.getEmail());

            } else {
                log.error("При восстановлении пароля для пользователя {} введен неверный код", request.getEmail());

                throw new AuthException(AuthError.builder()
                        .captcha("Код с картинки введён неверно")
                        .build()
                );
            }
        } catch (AuthException authEx) {
            response = new AuthResponse(false, (AuthError) authEx.getErrors());
        }

        return ResponseEntity.ok(response);
    }

    @SneakyThrows
    @Transactional
    public ResponseEntity<AuthResponse> register(AuthRequest request) {
        Boolean isMultiuserMode = settingService.getBooleanSettingValueByCode("MULTIUSER_MODE");

        if(!isMultiuserMode) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        AuthResponse response = new AuthResponse(true);

        try {
            if(request.getPassword().length() < 6) {
                throw new AuthException(AuthError.builder()
                                            .password("Пароль короче 6-ти символов")
                                            .build()
                            );
            }

            if(captchaService.checkCaptchaCode(request.getCaptcha(), request.getCaptchaSecret())) {

                if(userRepository.findByEmail(request.getEmail()).isPresent()) {
                    throw new AuthException(AuthError.builder()
                            .email("Этот e-mail уже зарегистрирован")
                            .build()
                    );
                }

                /*if(userRepository.findByFullName(request.getName()).isPresent()) {
                    throw new AuthException(AuthError.builder()
                            .name("Имя указано неверно")
                            .build()
                    );
                }*/

                User user = new User();
                user.setFullName(request.getName());
                user.setEmail(request.getEmail());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setModerator(false);

                userRepository.save(user);

                log.info("User {} successfully registered", request.getEmail());

            } else {
                throw new AuthException(AuthError.builder()
                        .captcha("Код с картинки введён неверно")
                        .build()
                );
            }
        } catch (AuthException authEx) {
              response = new AuthResponse(false, (AuthError) authEx.getErrors());
        }

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ProfileResponse> profile(MultipartFile photo,
                                                   String name,
                                                   String email,
                                                   String password,
                                                   int removePhoto) {
        String photoPath = "";
        try {

            if(photo != null && !photo.isEmpty()){
                BufferedImage image = ImageIO.read(photo.getInputStream());
                if(image.getWidth() > 36 || image.getHeight() > 36) {
                    byte[] imageBytes = imageService.imageResize(image, 36, 36);
                    photoPath = imageService.uploadImage(photo.getOriginalFilename(), imageBytes, ImageType.AVATAR);
                } else {
                    photoPath = imageService.uploadImage(photo.getOriginalFilename(), photo.getBytes(), ImageType.AVATAR);
                }

            }

        } catch(Exception ex) {
            ProfileError error = new ProfileError();
            error.setPhoto(ex.getMessage());
            ProfileResponse profileResponse = ProfileResponse.builder()
                    .result(false)
                    .errors(error)
                    .build();
            return ResponseEntity.ok(profileResponse);
        }

        ProfileRequest profileRequest = ProfileRequest.builder()
                .name(name)
                .email(email)
                .password(password)
                .pathPhoto(photoPath)
                .removePhoto(removePhoto)
                .build();
        ProfileResponse profileResponse = internalProfile(profileRequest);

        return ResponseEntity.ok(profileResponse);
    }

    public ResponseEntity<ProfileResponse> profile(ProfileRequest profileRequest) {
        ProfileResponse profileResponse = internalProfile(profileRequest);
        return ResponseEntity.ok(profileResponse);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Пользователь '%s' не найден!", email)));
    }

    @SneakyThrows
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Необходимо авторизоваться");
        }

        return  (User) authentication.getPrincipal();
    }

    private ProfileResponse internalProfile(ProfileRequest profileRequest) {
        User user = getCurrentUser();
        ProfileError error = new ProfileError();
        boolean isError = false;

        //проверка имени
        if(!StringUtils.isEmpty(profileRequest.getName())
                && profileRequest.getName().compareToIgnoreCase(user.getFullName())!=0) {
            if(profileRequest.getName().matches("^[0-9]+")){
                error.setName("Имя указано неверно");
                isError=true;
            } else {
                user.setFullName(profileRequest.getName());
            }
        }

        //проверка Email
        if(StringUtils.isEmpty(profileRequest.getEmail())) {
            error.setEmail("Нельзя затереть email");
            isError=true;
        } else if(profileRequest.getEmail().compareToIgnoreCase(user.getEmail())!=0) {
            if(userRepository.findByEmail(user.getEmail()).isPresent()) {
                error.setEmail("Этот e-mail уже зарегистрирован");
                isError=true;
            } else {
                user.setEmail(profileRequest.getEmail());
            }
        }

        //Проверка пароля
        if(!StringUtils.isEmpty(profileRequest.getPassword())) {
            if(profileRequest.getPassword().length() < 6) {
                error.setPassword("Пароль короче 6-ти символов");
                isError=true;
            } else {
                String encodingPassword = passwordEncoder.encode(profileRequest.getPassword());
                if (user.getPassword()
                        .compareTo(encodingPassword) == 0) {
                    error.setPassword("Пароль должен отличаться от текущего");
                    isError = true;
                } else {
                    user.setPassword(encodingPassword);
                }
            }
        }

        //Проверка фото
        if(profileRequest.getPathPhoto() != null
                    || profileRequest.getRemovePhoto() == 1) {
            if(user.getPhoto() != null) {
                imageService.deleteImageFromStorage(user.getPhoto());
            }
            user.setPhoto(profileRequest.getPathPhoto());
        }

        if(!isError) {
            userRepository.save(user);
            return ProfileResponse.builder()
                    .result(true)
                    .build();
        } else {
            return ProfileResponse.builder()
                    .result(false)
                    .errors(error)
                    .build();
        }
    }

    private UserResponse prepareUserResponse(User user) {
        UserResponse response = new UserResponse(UserMapper.toUserFullInfo(user));

        if(user.isModerator()) {
            response.getUser()
                    .setModerationCount(
                            postRepository.countByModerationStatusValueAndIsActive(ModerationStatus.NEW.toString()
                                    , true)
                    );
        }

        return  response;
    }

    @SneakyThrows
    private String generateRestoreHash() {
        String uuid = UUID.randomUUID().toString();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(uuid.getBytes());
        return DatatypeConverter
                .printHexBinary(md.digest())
                .toUpperCase();
    }
}
