package org.diplom.blog.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.diplom.blog.api.request.AuthRequest;
import org.diplom.blog.api.request.UserRequest;
import org.diplom.blog.api.response.AuthResponse;
import org.diplom.blog.api.response.CaptchaResponse;
import org.diplom.blog.api.response.CommonResponse;
import org.diplom.blog.api.response.UserResponse;
import org.diplom.blog.model.User;
import org.diplom.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
//    private final AuthenticationManager authenticationManager;//
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<UserResponse> login(UserRequest request) {
        /*Authentication auth = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        org.springframework.security.core.userdetails.User securityUser =
                (org.springframework.security.core.userdetails.User)auth.getPrincipal();*/

        /*userRepository.findByEmailAndPassword(request.getEmail(),
                                              passwordEncoder.encode(request.getPassword()));*/

        //userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());

        UserResponse response = new UserResponse();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<CommonResponse> logout() {
        CommonResponse response = new CommonResponse();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<UserResponse> check() {
        UserResponse response = new UserResponse();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<CommonResponse> restore(String email) {
        CommonResponse response = new CommonResponse();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<AuthResponse> password(AuthRequest request) {
        AuthResponse response = new AuthResponse(true);


        return ResponseEntity.ok(response);
    }

    public ResponseEntity<AuthResponse> register(AuthRequest request) {
        AuthResponse response = new AuthResponse(true);

        User user = new User();
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setModerator(false);

        userRepository.save(user);
        /*request.getCaptcha();
        request.getCaptchaSecret();
        request.getCode();*/

        log.info("User {} successfully registered", request.getEmail());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<CaptchaResponse> getCaptcha() {
        CaptchaResponse response = new CaptchaResponse("","");
        return ResponseEntity.ok(response);
    }

    /*public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email '" + email + "' not found"));
    }*/
}
