package org.diplom.blog.api.response;

import lombok.Data;
import org.diplom.blog.dto.TagDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class TagResponse {
    private List<TagDto> tags;

    public TagResponse(){
        tags = new ArrayList<>();
    }
}
