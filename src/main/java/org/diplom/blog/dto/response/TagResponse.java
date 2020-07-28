package org.diplom.blog.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diplom.blog.dto.model.Tag;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TagResponse {
    private List<Tag> tags;

    public TagResponse(){
        tags = new ArrayList<>();
    }
}
