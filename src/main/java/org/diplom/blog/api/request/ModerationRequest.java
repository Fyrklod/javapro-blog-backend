package org.diplom.blog.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diplom.blog.dto.Decision;

/*@Getter
@Setter
@NoArgsConstructor*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModerationRequest {
    @JsonProperty("post_id")
    private final Long postId;
    private final Decision decision;
}
