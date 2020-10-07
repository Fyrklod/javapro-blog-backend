package org.diplom.blog.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.diplom.blog.dto.Error;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadResponse extends CommonResponse {
    private final Error errors;

    @Builder
    public UploadResponse(boolean result, Error errors){
        super(result);
        this.errors = errors;
    }
}
