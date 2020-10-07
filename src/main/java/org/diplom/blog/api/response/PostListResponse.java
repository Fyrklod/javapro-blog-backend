package org.diplom.blog.api.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
public class PostListResponse {
    private final Long count;
    private final List<PostResponse> posts;
}
