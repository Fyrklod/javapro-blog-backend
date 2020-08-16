package org.diplom.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long id;
    private String name;
    private String photo;
    private Integer removePhoto;
    private String email;
    private Boolean moderation;
    private Integer moderationCount;
    private Boolean settings;
}
