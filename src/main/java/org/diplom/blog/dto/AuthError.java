package org.diplom.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class AuthError {
    private final String email;
    private final String photo;
    private final String name;
    private final String code;
    private final String password;
    private final String captcha;
}
