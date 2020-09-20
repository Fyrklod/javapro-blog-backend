package org.diplom.blog.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
public class PostListResponse {
    private final Long count;
    private final List<PostResponse> posts;
}
