package org.diplom.blog.dto.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class User {
    private Integer id;
    private String name;
    private String photo;
    private Integer removePhoto;
    private String email;
    private Boolean moderation;
    private Integer moderationCount;
    private Boolean settings;
}
