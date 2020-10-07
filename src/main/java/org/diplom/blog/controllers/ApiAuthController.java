package org.diplom.blog.controllers;

import lombok.RequiredArgsConstructor;
import org.diplom.blog.api.request.AuthRequest;
import org.diplom.blog.api.request.UserRequest;
import org.diplom.blog.api.response.*;
import org.diplom.blog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest request) {
        return userService.login(request);
    }

    @GetMapping("/logout")
    public ResponseEntity<CommonResponse> logout() {
        return userService.logout();
    }

    @GetMapping("/check")
    public ResponseEntity<UserResponse> check() {
        return userService.check();
    }

    @PostMapping("/restore")
    public ResponseEntity<CommonResponse> restore(String email) {
        return userService.restore(email);
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
        return userService.getCaptcha();
    }
}
