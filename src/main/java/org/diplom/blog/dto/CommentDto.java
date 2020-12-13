package org.diplom.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {
    private final Long id;
    private final long timestamp;
    private final String text;
    private final UserInfo user;
}
