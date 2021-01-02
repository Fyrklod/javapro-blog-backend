package org.diplom.blog.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.diplom.blog.dto.Decision;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModerationRequest {
    @JsonProperty("post_id")
    private final Long postId;
    private final Decision decision;
}
