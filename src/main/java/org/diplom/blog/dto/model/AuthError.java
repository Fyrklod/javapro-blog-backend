package org.diplom.blog.dto.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class AuthError {
    private String email;
    private String photo;
    private String name;
    private String code;
    private String password;
    private String captcha;
}
