package org.diplom.blog.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthRequest {
    @JsonProperty("e_mail")
    private final String email;
    private final String code;
    private final String password;
    private final String name;
    private final String captcha;
    @JsonProperty("captcha_secret")
    private final String captchaSecret;
}
