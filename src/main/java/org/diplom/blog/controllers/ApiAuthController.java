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

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest request) {
        return userService.login(request);
    }

    @GetMapping("/logout")
    public ResponseEntity<SimpleResponse> logout(HttpServletRequest httpRequest,
                                                 HttpServletResponse httpResponse) {
        return userService.logout(httpRequest, httpResponse);
    }

    @GetMapping("/check")
    public ResponseEntity<UserResponse> check() {
        return userService.check();
    }

    @PostMapping("/restore")
    public ResponseEntity<SimpleResponse> restore(@RequestBody RecoveryRequest request) {
        return userService.restore(request);
    }

    @PostMapping("/password")
    public ResponseEntity<AuthResponse> password(@RequestBody AuthRequest request) {
        return userService.password(request);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        return userService.register(request);
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() {
        return captchaService.getCaptcha();
    }
}
