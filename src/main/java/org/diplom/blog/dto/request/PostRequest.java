package org.diplom.blog.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRequest {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm, dd.MM.yyyy")
    private Date time;
    private boolean active;
    private Set<String> tags;
    private String text;
}
