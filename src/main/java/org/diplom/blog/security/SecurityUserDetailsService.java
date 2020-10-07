package org.diplom.blog.security;

import lombok.extern.slf4j.Slf4j;
import org.diplom.blog.model.User;
import org.diplom.blog.repository.UserRepository;

//import org.springframework.security.core.userdetails.User;
import org.diplom.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Andrey.Kazakov
 * @date 24.09.2020
 */
@Slf4j
@Service("securityService")
public class SecurityUserDetailsService implements UserDetailsService {

    //private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public SecurityUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userService.findByEmail(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email '" + email + "' not found"));
        return SecurityUserDetails.fromUser(user);
    }
}
