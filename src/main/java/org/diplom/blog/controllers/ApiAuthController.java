package org.diplom.blog.controllers;

import org.diplom.blog.dto.request.*;
import org.diplom.blog.dto.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest request) {
        UserResponse response = new UserResponse();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<Boolean> logout() {
        boolean result = true;
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/check")
    public ResponseEntity<UserResponse> check() {
        UserResponse response = new UserResponse();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/restore")
    public ResponseEntity<Boolean> restore(String email) {
        boolean result = false;

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/password")
    public ResponseEntity<AuthResponse> password(@RequestBody AuthRequest request) {
        AuthResponse response = new AuthResponse();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        AuthResponse response = new AuthResponse();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() {
        CaptchaResponse response = new CaptchaResponse();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
