package org.diplom.blog.api.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/*@Getter
@Setter
@NoArgsConstructor*/
@Data
@Accessors(chain = true)
public class StatisticsResponse {
    private long postsCount;
    private long likesCount;
    private long dislikesCount;
    private long viewsCount;
    private long firstPublication;
}
