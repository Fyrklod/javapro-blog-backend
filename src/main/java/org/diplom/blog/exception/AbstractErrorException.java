package org.diplom.blog.exception;

import lombok.Getter;
import lombok.SneakyThrows;
import org.diplom.blog.dto.AbstractError;

import java.lang.reflect.Field;

/**
 * @author Andrey.Kazakov
 * @date 29.11.2020
 */
@Getter
public abstract class AbstractErrorException extends Exception {
    private final AbstractError errors;

    public AbstractErrorException(AbstractError errors) {
        super(getErrorExceptionMessage(errors));
        this.errors = errors;
    }

    @SneakyThrows
    private static String getErrorExceptionMessage(AbstractError error)  {
        StringBuilder errorString = new StringBuilder();
        for (Field field : error.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if(field.get(error) != null) {
                errorString.append(String.format("%s: %s\n", field.getName(), field.get(error)));
            }
        }

        return errorString.toString();
    }
}
