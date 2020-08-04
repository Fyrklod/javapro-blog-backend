package org.diplom.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StatisticsResponse {
    private Integer postsCount;
    private Integer likesCount;
    private Integer dislikesCount;
    private Integer viewsCount;
    private long firstPublication;
}
