package org.diplom.blog.dto.response;

import org.diplom.blog.dto.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostsResponse {
    private Integer count;
    private List<Post> posts;

    public PostsResponse(){
        posts = new ArrayList<>();
    }
}
