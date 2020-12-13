package org.diplom.blog.exception;

import lombok.Data;
import lombok.SneakyThrows;
import org.diplom.blog.dto.AuthError;

import java.lang.reflect.Field;

/**
 * @author Andrey.Kazakov
 * @date 25.10.2020
 */
public class AuthException extends AbstractErrorException {
    public AuthException(AuthError errors) {
        super(errors);
    }
}
/*public class AuthException extends Exception {
    private final AuthError errors;

    public AuthException(AuthError errors) {
        super(getAuthExceptionMessage(errors));
        this.errors = errors;
    }

    @SneakyThrows
    private static String getAuthExceptionMessage(AuthError error)  {
        StringBuilder authErrorString = new StringBuilder();
        for (Field field : error.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if(field.get(error) != null) {
                authErrorString.append(String.format("%s: %s\n", field.getName(), field.get(error)));
            }
        }

        return authErrorString.toString();
    }
}*/
