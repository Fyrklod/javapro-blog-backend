package org.diplom.blog.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/*@Getter
@Setter
@NoArgsConstructor*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRequest {
    private final long timestamp;
    private final boolean active;
    private final String title;
    private final String[] tags;
    private final String text;
}
