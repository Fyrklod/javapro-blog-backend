package org.diplom.blog.api.response;

import lombok.Builder;
import org.diplom.blog.dto.AuthError;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse extends CommonResponse<AuthError> {

    @Builder
    public AuthResponse(boolean result){
        super(result);
    }

    @Builder
    public AuthResponse(boolean result, AuthError errors){
        super(result, errors);
    }
}
