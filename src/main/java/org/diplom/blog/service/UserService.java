package org.diplom.blog.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.diplom.blog.api.request.RecoveryRequest;
import org.diplom.blog.exception.AuthException;
import org.diplom.blog.api.request.AuthRequest;
import org.diplom.blog.api.request.UserRequest;
import org.diplom.blog.api.response.AuthResponse;
import org.diplom.blog.api.response.CommonResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserService {

    @Value("${blog.info.title}")
    private String siteTitle;
    private final ConcurrentHashMap userSessionMap;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CaptchaService captchaService;
    private final MailSenderService mailSenderService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       CaptchaService captchaService,
                       PostRepository postRepository,
                       MailSenderService mailSenderService,
                       @Lazy AuthenticationManager authenticationManager){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.captchaService = captchaService;
        this.postRepository = postRepository;
        this.mailSenderService = mailSenderService;
        this.authenticationManager = authenticationManager;
        this.userSessionMap = new ConcurrentHashMap();
    }

    public ResponseEntity<UserResponse> login(UserRequest request) {
        /*Метод проверяет введенные данные и производит авторизацию пользователя, если введенные данные верны.
        Если пользователь авторизован, ???идентификатор его сессии должен запоминаться в Map<String, Integer>
        со значением, равным ID пользователя, которому принадлежит данная сессия.????*/

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        User currentUser = (User)auth.getPrincipal();
        //userSessionMap.put("SESSION", currentUser.getId());///?????
        return ResponseEntity.ok(prepareUserResponse(currentUser));
    }

    public ResponseEntity<CommonResponse> logout() {
        SecurityContextHolder.clearContext();

        CommonResponse response = new CommonResponse();
        response.setResult(true);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<UserResponse> check() {
        try{
            User currentUser = getCurrentUser();
            return ResponseEntity.ok(prepareUserResponse(currentUser));
        } catch (AccessDeniedException accessEx) {
            return ResponseEntity.ok(new UserResponse());
        }
    }

    public ResponseEntity<CommonResponse> restore(RecoveryRequest request) {
        CommonResponse response = new CommonResponse();

        try {
            String restoreHash = generateRestoreHash();
            User user = getUserByEmail(request.getEmail());
            user.setCode(restoreHash);
            userRepository.save(user);

            //TODO:формирования адреса должно использовать конфиг файл server.port и server.host ?
            String urlForRestore = String.format("http://localhost:9090/login/change-password/%s",
                    restoreHash);

            String letterText = String.format("Добрый день, %s\n" +
                    "\n" +
                    "Вы запросили восстановление пароля на нашем сайте. Для продолжения пройдите по адресу:\n" +
                    "<a href=\"%s\">%s</a>\n" +
                    "С уважением,\n" +
                    "Команда \"%s\"", user.getFullName(), urlForRestore, urlForRestore, siteTitle);
            //CompletableFuture.runAsync(() -> mailSenderService.send(user.getEmail(), "Восстановление пароля",
            //        letterText));
            mailSenderService.send(user.getEmail(), "Восстановление пароля", letterText);
            response.setResult(true);
        } catch(Exception ex) {
            log.info("User {} not found for restore", request.getEmail());
            response.setResult(false);
        }

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<AuthResponse> password(AuthRequest request) {
        AuthResponse response = new AuthResponse(true);

        try {
            if(request.getPassword().length() < 6) {
                throw new AuthException(AuthError.builder()
                        .password("Пароль короче 6-ти символов")
                        .build()
                );
            }

            if(captchaService.checkCaptchaCode(request.getCaptcha(), request.getCaptchaSecret())) {
                User user = userRepository.findByCode(request.getCode())
                        .orElseThrow(() -> new AuthException(AuthError.builder()
                                .code("Ссылка для восстановления пароля устарела. " +
                                        "<a href=\"/auth/restore\">Запросить ссылку снова</a>")
                                .build()
                        ));


                user.setCode(null);
                user.setPassword(passwordEncoder.encode(request.getPassword()));

                userRepository.save(user);

                log.info("Password for user {} recovered successfully", user.getEmail());

            } else {
                throw new AuthException(AuthError.builder()
                        .captcha("Код с картинки введён неверно")
                        .build()
                );
            }
        } catch (AuthException authEx) {
            response = new AuthResponse(false, authEx.getErrors());
        }

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<AuthResponse> register(AuthRequest request) {
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
              response = new AuthResponse(false, authEx.getErrors());
        }

        return ResponseEntity.ok(response);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email '%s' not found", email)));
    }

    @SneakyThrows
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Необходимо авторизоваться");
        }

        return  (User) authentication.getPrincipal();
    }

    private UserResponse prepareUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUser(UserMapper.toUserDto(user));
        if(user.isModerator()) {
            response.getUser().setModerationCount(postRepository.countByModerationStatusValueAndIsActive(ModerationStatus.NEW.toString(), true));
        }
        response.setResult(true);
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
