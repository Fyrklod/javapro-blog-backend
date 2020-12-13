package org.diplom.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UserInfo {
    private final Long id;
    private final String name;
    private final String photo;
    private final String email;
    private final Boolean moderation;
    private Long moderationCount;
    private final Boolean settings;

    public void setModerationCount(Long moderationCount) {
        this.moderationCount = moderationCount;
    }
}
