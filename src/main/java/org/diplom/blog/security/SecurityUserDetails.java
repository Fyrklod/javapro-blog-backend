package org.diplom.blog.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.diplom.blog.model.Role;
import org.diplom.blog.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Andrey.Kazakov
 * @date 24.09.2020
 */
@Data
public class SecurityUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<SimpleGrantedAuthority> authorities;
    private final Date lastPasswordRestDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static UserDetails fromUser(User user){
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().getAuthorities())
                .build();
    }
}
