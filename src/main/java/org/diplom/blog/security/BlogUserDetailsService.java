package org.diplom.blog.security;

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
        return userService.getUserByEmail(email);
    }
}
