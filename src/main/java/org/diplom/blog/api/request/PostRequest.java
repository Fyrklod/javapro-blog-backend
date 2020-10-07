package org.diplom.blog.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRequest {
    private long timestamp;
    private boolean active;
    private String title;
    private String[] tags;
    private String text;
}
