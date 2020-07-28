package org.diplom.blog.dto.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {
    private Integer id;
    //TODO: формат для  "Вчера, 17:32"
    private Date time;
    private String text;
    private User user;
}
