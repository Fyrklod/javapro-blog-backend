package org.diplom.blog.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.Nullable;
import lombok.*;
import org.diplom.blog.dto.AbstractError;

/**
 * @author Andrey.Kazakov
 * @date 12.08.2020
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T extends AbstractError> {
    private final boolean result;
    private final T errors;

    public CommonResponse(boolean result) {
        this(result, null);
    }

    public CommonResponse(boolean result, @Nullable T errors) {
        this.result = result;
        this.errors = errors;
    }
}
