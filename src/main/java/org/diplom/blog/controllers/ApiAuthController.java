package org.diplom.blog.controllers;

import lombok.RequiredArgsConstructor;
import org.diplom.blog.api.request.AuthRequest;
import org.diplom.blog.api.request.RecoveryRequest;
import org.diplom.blog.api.request.UserRequest;
import org.diplom.blog.api.response.*;
import org.diplom.blog.dto.mapper.UserMapper;
import org.diplom.blog.model.User;
import org.diplom.blog.service.CaptchaService;
import org.diplom.blog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    public ResponseEntity<CommonResponse> logout() {
        return userService.logout();
    }

    @GetMapping("/check")
    public ResponseEntity<UserResponse> check() {
        return userService.check();
    }

    @PostMapping("/restore")
    public ResponseEntity<CommonResponse> restore(@RequestBody RecoveryRequest request) {
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
