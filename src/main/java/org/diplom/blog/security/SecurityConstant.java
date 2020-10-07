package org.diplom.blog.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Andrey.Kazakov
 * @date 03.10.2020
 */
@Component
public class SecurityConstant {

    public static String HEADER_NAME;
    public static String TOKEN_PREFIX;
    public static int EXPIRATION_TIME;
    public static String SECRET;
    public static String LOGIN_PATH;

    @Value("${security.jwt.header}")
    private void setHeaderName(String headerName) {
        HEADER_NAME = headerName;
    }

    @Value("${security.jwt.prefix}")
    private void setTokenPrefix(String tokenPrefix) {
        TOKEN_PREFIX = tokenPrefix;
    }

    @Value("${security.jwt.expiration}")
    private void setExpirationTime(int expirationTime) {
        EXPIRATION_TIME = expirationTime;
    }

    @Value("${security.jwt.secret}")
    private void setSECRET(String SECRET) {
        SecurityConstant.SECRET = SECRET;
    }

    @Value("${security.jwt.url-authentication}")
    private void setLoginPath(String loginPath) {
        LOGIN_PATH = loginPath;
    }
}
