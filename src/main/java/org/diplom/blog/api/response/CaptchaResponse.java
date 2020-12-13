package org.diplom.blog.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaptchaResponse {
    private final String secret;
    private final String image;
}
