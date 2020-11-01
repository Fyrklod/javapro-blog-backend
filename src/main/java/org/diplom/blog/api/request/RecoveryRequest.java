package org.diplom.blog.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Andrey.Kazakov
 * @date 29.10.2020
 */
@Getter
public class RecoveryRequest {
    private String email;
}
