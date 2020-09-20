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
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<CommonResponse> logout() {
        CommonResponse response = new CommonResponse();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<UserResponse> check() {
        UserResponse response = new UserResponse();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/restore")
    public ResponseEntity<CommonResponse> restore(String email) {
        CommonResponse response = new CommonResponse();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password")
    public ResponseEntity<AuthResponse> password(@RequestBody AuthRequest request) {
        AuthResponse response = new AuthResponse(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        AuthResponse response = new AuthResponse(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> getCaptcha() {
        CaptchaResponse response = new CaptchaResponse("","");
        return ResponseEntity.ok(response);
    }
}
