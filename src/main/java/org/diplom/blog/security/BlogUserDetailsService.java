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
@Service("blogService")
public class BlogUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public BlogUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(email);
        return user;//BlogUserDetails.fromUser(user);
    }
}
