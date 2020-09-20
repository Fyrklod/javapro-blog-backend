package org.diplom.blog.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

/**
 * @author Andrey.Kazakov
 * @date 16.08.2020
 */
@Getter
public class CalendarResponse {
    private final Set<Integer> years;
    private final Map<String, Long> posts;

    public CalendarResponse(Set<Integer> years, Map<String, Long> posts){
        this.years = years;
        this.posts = posts;
    }
}
