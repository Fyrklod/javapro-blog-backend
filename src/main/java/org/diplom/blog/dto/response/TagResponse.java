package org.diplom.blog.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.diplom.blog.dto.TagDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TagResponse {
    private List<TagDto> tags;

    public TagResponse(){
        tags = new ArrayList<>();
    }
}
