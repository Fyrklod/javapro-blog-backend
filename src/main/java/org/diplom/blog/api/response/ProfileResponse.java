package org.diplom.blog.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.diplom.blog.dto.ProfileError;

/**
 * @author Andrey.Kazakov
 * @date 29.11.2020
 */
//@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponse extends CommonResponse<ProfileError> {
    @Builder
    public ProfileResponse(boolean result, ProfileError errors) {
        super(result, errors);
    }
}
