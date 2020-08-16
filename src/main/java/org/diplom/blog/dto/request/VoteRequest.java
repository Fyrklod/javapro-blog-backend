package org.diplom.blog.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
