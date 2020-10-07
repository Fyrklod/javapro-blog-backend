package org.diplom.blog.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Andrey.Kazakov
 * @date 25.09.2020
 */
public enum Role {
    MODERATOR(Set.of(Permission.READ, Permission.WRITE, Permission.APPROVE)),
    USER(Set.of(Permission.READ, Permission.WRITE));

    private final Set<Permission> permissions;

    private Role(Set<Permission> permissions){
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission()))
                .collect(Collectors.toSet());
    }
}
