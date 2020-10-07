package org.diplom.blog.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Andrey.Kazakov
 * @date 12.08.2020
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteRequest {
    @JsonProperty("post_id")
    private Long postId;
}
