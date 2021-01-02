package org.diplom.blog.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.diplom.blog.dto.ImageError;

/**
 * @author Andrey.Kazakov
 * @date 10.12.2020
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageResponse extends CommonResponse<ImageError> {
    @Builder
    public ImageResponse(boolean result, ImageError errors){
        super(result, errors);
    }
}
