package org.diplom.blog.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.diplom.blog.api.request.*;
import org.diplom.blog.api.response.*;
import org.diplom.blog.dto.ImageType;
import org.diplom.blog.dto.ProfileError;
import org.diplom.blog.exception.AuthException;
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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

@Slf4j
@Service
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

    /**
     * Метод login - авторизация пользователя.
     *
     * @param userRequest тело запроса в формате Json.
     * @return ResponseEntity<UserResponse> .
     * @see UserRequest ;
     * @see UserResponse ;
     */
    public ResponseEntity<UserResponse> login(UserRequest userRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            User currentUser = (User)auth.getPrincipal();
            return ResponseEntity.ok(prepareUserResponse(currentUser));

        } catch (AuthenticationException authEx) {
            log.error("Пользователю {} не удалось авторизоваться по причине ошибки {}", userRequest.getEmail()
                                                                                      , authEx.getMessage());
            return ResponseEntity.ok(new UserResponse());
        }
    }

    /**
     * Метод logout - выход.
     *
     * @param httpRequest HTTPServlet запроса.
     * @param httpResponse HTTPServlet ответа.
     * @return ResponseEntity<SimpleResponse> .
     * @see SimpleResponse;
     */
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

    /**
     * Метод check - проверка статуса авторизации.
     *
     * @return ResponseEntity<UserResponse> .
     * @see UserResponse;
     */
    public ResponseEntity<UserResponse> check() {
        try{
            User currentUser = getCurrentUser();
            return ResponseEntity.ok(prepareUserResponse(currentUser));
        } catch (AuthenticationException accessEx) {
            return ResponseEntity.ok(new UserResponse());
        }
    }

    /**
     * Метод restore - восстановление пароля.
     *
     * @param recoveryRequest тело запроса в формате Json.
     * @return ResponseEntity<SimpleResponse> .
     * @see RecoveryRequest;
     * @see SimpleResponse;
     */
    public ResponseEntity<SimpleResponse> restore(RecoveryRequest recoveryRequest) {
        boolean result;

        try {
            String restoreHash = generateRestoreHash();
            User user = getUserByEmail(recoveryRequest.getEmail());
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
            mailSenderService.sendMail(user.getEmail(), "Восстановление пароля", letterText);

            result = true;
        } catch(Exception ex) {
            log.info("User {} not found for restore", recoveryRequest.getEmail());
            result = false;
        }

        return ResponseEntity.ok(new SimpleResponse(result));
    }

    /**
     * Метод password - смена старого пароля пользователя.
     *
     * @param authRequest тело запроса в формате Json.
     * @return ResponseEntity<AuthResponse>.
     * @see AuthRequest;
     * @see AuthResponse;
     */
    @Transactional
    public ResponseEntity<AuthResponse> password(AuthRequest authRequest) {
        AuthResponse response = new AuthResponse(true);

        try {
            if(authRequest.getPassword().length() < 6) {
                log.error("При восстановлении пароля для пользователя {} введен некорректный пароль", authRequest.getEmail());

                throw new AuthException(AuthError.builder()
                        .password("Пароль короче 6-ти символов")
                        .build()
                );
            }

            if(captchaService.checkCaptchaCode(authRequest.getCaptcha(), authRequest.getCaptchaSecret())) {
                User user = userRepository.findByCode(authRequest.getCode())
                        .orElseThrow(() -> new AuthException(AuthError.builder()
                                .code("Ссылка для восстановления пароля устарела. " +
                                        "<a href=\"/login/restore-password\">Запросить ссылку снова</a>")
                                .build()
                        ));


                user.setCode(null);
                user.setPassword(passwordEncoder.encode(authRequest.getPassword()));

                userRepository.save(user);

                log.info("Пароль для пользователя {} успешно восстановлен", user.getEmail());

            } else {
                log.error("При восстановлении пароля для пользователя {} введен неверный код", authRequest.getEmail());

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

    /**
     * Метод register - регистрация нового пользователя.
     * Регистрация возможно, если на сайте включен много пользовательский режим
     *
     * @param authRequest тело запроса в формате Json.
     * @return ResponseEntity<AuthResponse> .
     * @see AuthRequest;
     * @see AuthResponse;
     */
    @SneakyThrows
    @Transactional
    public ResponseEntity<AuthResponse> register(AuthRequest authRequest) {
        Boolean isMultiuserMode = settingService.getBooleanSettingValueByCode("MULTIUSER_MODE");

        if(!isMultiuserMode) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        AuthResponse response = new AuthResponse(true);

        try {
            if(authRequest.getPassword().length() < 6) {
                throw new AuthException(AuthError.builder()
                                            .password("Пароль короче 6-ти символов")
                                            .build()
                            );
            }

            if(captchaService.checkCaptchaCode(authRequest.getCaptcha(), authRequest.getCaptchaSecret())) {

                if(userRepository.findByEmail(authRequest.getEmail()).isPresent()) {
                    throw new AuthException(AuthError.builder()
                            .email("Этот e-mail уже зарегистрирован")
                            .build()
                    );
                }

                User user = new User();
                user.setFullName(authRequest.getName());
                user.setEmail(authRequest.getEmail());
                user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
                user.setModerator(false);

                userRepository.save(user);

                log.info("Пользователь {} успешно зарегистрирован", authRequest.getEmail());

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

    /**
     * Метод profile - редактирование профиля
     *
     * @param photo обновленная аватарка в формате Multipart form-data
     * @param name новое имя пользователя
     * @param email новый email
     * @param password новый пароль
     * @param removePhoto признак удаления аватарки.
     * @return ResponseEntity<ProfileResponse>
     * @see ProfileRequest;
     * @see ProfileResponse;
     */
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

    /**
     * Метод profile - редактирование профиля
     *
     * @param profileRequest тело запроса в формате Json.
     * @return ResponseEntity<ProfileResponse>
     * @see ProfileRequest;
     * @see ProfileResponse;
     */
    public ResponseEntity<ProfileResponse> profile(ProfileRequest profileRequest) {
        ProfileResponse profileResponse = internalProfile(profileRequest);
        return ResponseEntity.ok(profileResponse);
    }

    /**
     * Метод getUserByEmail - получение пользователя по Email регистрации.
     *
     * @param email - email пользователя.
     * @return User - пользователь .
     * @see User;
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Пользователь '%s' не найден!", email)));
    }

    /**
     * Метод getCurrentUser - получает текущего пользователя.
     * Если пользователь не авторизован, то метод свалится в ошибку BadCredentialsException
     *
     * @return User - текущий пользователь
     * @see User;
     */
    @SneakyThrows
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new BadCredentialsException("Необходимо авторизоваться");
        }

        return  (User) authentication.getPrincipal();
    }

    /**
     * Метод internalProfile - внутренний метод для редактирования профиля.
     *
     * @param profileRequest тело запроса в формате Json.
     * @return ProfileResponse - сервисный ответ с информацией о профиле.
     * @see ProfileRequest;
     * @see ProfileResponse;
     */
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
                imageService.deleteImage(user.getPhoto());
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

    /**
     * Метод prepareUserResponse - подготовлка сервисного ответа с данными пользователя.
     * Данный метод оборачивает модель пользователя в сообщение типа ответ дополняя новыми полями
     *
     * @param user - пользователь.
     * @return UserResponse - информация о пользователе.
     * @see User;
     * @see UserResponse;
     */
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

    /**
     * Метод generateRestoreHash - генерация Хэш-кода для восстановления пароля.
     *
     * @return сгенерированный код.
     */
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
