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
@Setter
public class CalendarResponse {
    private Set<Integer> years;
    private List<Map<String, Integer>> posts;

    public CalendarResponse(Integer year){
        posts = new ArrayList<>();
        years = new HashSet<>();
        years.add(year);
    }
}
