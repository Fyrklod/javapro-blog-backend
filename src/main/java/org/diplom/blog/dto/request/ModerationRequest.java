package org.diplom.blog.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diplom.blog.dto.model.Decision;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModerationRequest {
    @JsonProperty("post_id")
    private Integer postId;
    private Decision decision;
}
