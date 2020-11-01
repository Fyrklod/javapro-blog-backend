package org.diplom.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long id;
    private String name;
    private String photo;
    private Integer removePhoto;
    private String email;
    private Boolean moderation;
    private Long moderationCount;
    private Boolean settings;
}
