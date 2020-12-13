package org.diplom.blog.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.diplom.blog.dto.CommentDto;
import org.diplom.blog.dto.UserInfo;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {
    private final Long id;
    private final Long timestamp;
    @JsonProperty("active")
    private final Boolean isActive;
    private final UserInfo user;
    private final String title;
    private final String announce;
    private final String text;
    private final Integer likeCount;
    private final Integer dislikeCount;
    private final Integer commentCount;
    private final Integer viewCount;
    private final List<CommentDto> comments;
    private final List<String> tags;
}

