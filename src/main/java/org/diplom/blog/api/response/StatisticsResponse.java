package org.diplom.blog.api.response;

import lombok.*;
import lombok.experimental.Accessors;

/*@Getter
@Setter
@NoArgsConstructor*/
@Getter
@Builder
//@Accessors(chain = true)
public class StatisticsResponse {
    private final long postsCount;
    private final long likesCount;
    private final long dislikesCount;
    private final long viewsCount;
    private final long firstPublication;
}
