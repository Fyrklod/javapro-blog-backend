package org.diplom.blog.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentRequest {
    @JsonProperty("parent_id")
    private Long parentId;
    @JsonProperty("post_id")
    private Long postId;
    private String text;
}
