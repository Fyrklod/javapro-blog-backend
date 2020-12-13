package org.diplom.blog.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*@Getter
@Setter
@NoArgsConstructor*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest {
    @JsonProperty("e_mail")
    private final String email;
    private final String password;
}
