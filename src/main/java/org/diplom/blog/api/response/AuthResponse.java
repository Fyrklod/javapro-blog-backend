package org.diplom.blog.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.diplom.blog.dto.AuthError;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse extends CommonResponse {
    private AuthError errors;

    @Builder
    public AuthResponse(boolean result){
        super(result);
    }

    @Builder
    public AuthResponse(boolean result, AuthError errors){
        this(result);
        this.errors = errors;
    }
}
