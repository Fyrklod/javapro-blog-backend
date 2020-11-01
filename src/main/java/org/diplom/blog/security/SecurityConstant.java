package org.diplom.blog.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author Andrey.Kazakov
 * @date 03.10.2020
 */
@Component
public class SecurityConstant {

    public static String AUTHORIZATION_HEADER;
    public static String TOKEN_PREFIX;
    public static Long EXPIRATION_TIME;
    public static String SECRET_KEY;
    public static String LOGIN_PATH;
    public static Integer PASSWORD_LEVEL_SECURITY;

    @Value("${security.jwt.authorization-header-name}")
    private void setAuthorizationHeader(String authorizationHeader) {
        AUTHORIZATION_HEADER = authorizationHeader;
    }

    @Value("${security.jwt.prefix}")
    private void setTokenPrefix(String tokenPrefix) {
        TOKEN_PREFIX = tokenPrefix;
    }

    @Value("${security.jwt.token-expiration-day}")
    private void setExpirationTime(long expirationTime) {
        EXPIRATION_TIME = expirationTime;
    }

    @Value("${security.jwt.secret}")
    private void setSecretKey(String secretKey) {
        SecurityConstant.SECRET_KEY = secretKey;
    }

    @Value("${security.jwt.url-authentication}")
    private void setLoginPath(String loginPath) {
        LOGIN_PATH = loginPath;
    }

    @Value("${security.jwt.password-level-security}")
    private void setLevelSecurity(int levelSecurity) {
        PASSWORD_LEVEL_SECURITY = levelSecurity;
    }
}
