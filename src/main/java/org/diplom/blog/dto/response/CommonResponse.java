package org.diplom.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Andrey.Kazakov
 * @date 12.08.2020
 */
@Getter
@Setter
public class CommonResponse {
    private boolean result;
}
