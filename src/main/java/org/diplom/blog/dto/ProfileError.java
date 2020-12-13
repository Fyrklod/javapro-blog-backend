package org.diplom.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * @author Andrey.Kazakov
 * @date 29.11.2020
 */
/*@Getter
@Setter
@NoArgsConstructor*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileError extends AbstractError {
    private String email;
    private String photo;
    private String name;
    private String password;
}
