package org.diplom.blog.controllers;

import lombok.RequiredArgsConstructor;
import org.diplom.blog.api.request.AuthRequest;
import org.diplom.blog.api.request.RecoveryRequest;
import org.diplom.blog.api.request.UserRequest;
import org.diplom.blog.api.response.*;
import org.diplom.blog.service.CaptchaService;
import org.diplom.blog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final UserService userService;
    private final CaptchaService captchaService;

    /**
     * Метод login - Вход.
     * Метод проверяет введенные данные и производит авторизацию пользователя, если введенные данные верны.
     * POST запрос /api/auth/login
     *
     * @param userRequest тело запроса в формате Json.
     * @return ResponseEntity<UserResponse>.
     * @see UserRequest;
     * @see UserResponse;
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest userRequest) {
        return userService.login(userRequest);
    }

    /**
     * Метод logout - Выход.
     * Метод разлогинивает пользователя: удаляет идентификатор его сессии из списка авторизованных.
     * Get запрос /api/auth/logout
     *
     * @param httpRequest servlet запроса.
     * @param httpResponse servlet запроса.
     * @return ResponseEntity<SimpleResponse>.
     * @see SimpleResponse;
     */
    @GetMapping("/logout")
    public ResponseEntity<SimpleResponse> logout(HttpServletRequest httpRequest,
                                                 HttpServletResponse httpResponse) {
        return userService.logout(httpRequest, httpResponse);
    }

    /**
     * Метод check - Статус авторизации.
     * Метод возвращает информацию о текущем авторизованном пользователе, если он авторизован.
     * Get запрос /api/auth/check
     *
     * @return ResponseEntity<UserResponse>.
     * @see UserResponse;
     */
    @GetMapping("/check")
    public ResponseEntity<UserResponse> check() {
        return userService.check();
    }

    /**
     * Метод restore - Восстановление пароля.
     * Метод проверяет наличие в базе пользователя с указанным e-mail. Если пользователь найден,
     * ему должно отправляться письмо со ссылкой на восстановление пароля
     * POST запрос /api/auth/restore
     *
     * @param recoveryRequest тело запроса в формате Json.
     * @return ResponseEntity<SimpleResponse>.
     * @see RecoveryRequest;
     * @see SimpleResponse;
     */
    @PostMapping("/restore")
    public ResponseEntity<SimpleResponse> restore(@RequestBody RecoveryRequest recoveryRequest) {
        return userService.restore(recoveryRequest);
    }

    /**
     * Метод password - Изменение пароля.
     * Метод проверяет корректность кода восстановления пароля (параметр code) и корректность кодов капчи.
     * POST запрос /api/auth/password
     *
     * @param authRequest тело запроса в формате Json.
     * @return ResponseEntity<AuthResponse>.
     * @see AuthRequest;
     * @see AuthResponse;
     */
    @PostMapping("/password")
    public ResponseEntity<AuthResponse> password(@RequestBody AuthRequest authRequest) {
        return userService.password(authRequest);
    }

    /**
     * Метод register - Регистрация.
     * Метод создаёт пользователя в базе данных, если введённые данные верны.
     * POST запрос /api/auth/register
     *
     * @param authRequest тело запроса в формате Json.
     * @return ResponseEntity<AuthResponse>.
     * @see AuthRequest;
     * @see AuthResponse;
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest authRequest) {
        return userService.register(authRequest);
    }

    /**
     * Метод getCaptcha - Запрос каптчи.
     * Метод генерирует коды капчи, - отображаемый и секретный, - сохраняет их в базу данных (таблица captcha_codes)
     * и возвращает секретный код secret. Также метод должен удалять устаревшие капчи из таблицы. Время устаревания
     * должно быть задано в конфигурации приложения (по умолчанию, 1 час).
     * Get запрос /api/auth/captcha
     *
     * @return ResponseEntity<CaptchaResponse>.
     * @see CaptchaResponse;
     */
    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() {
        return captchaService.getCaptcha();
    }
}
