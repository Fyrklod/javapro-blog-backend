package org.diplom.blog.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

/**
 * @author Andrey.Kazakov
 * @date 12.08.2020
 */
@Getter
public class VoteRequest {
    @JsonProperty("post_id")
    private Long postId;
}
