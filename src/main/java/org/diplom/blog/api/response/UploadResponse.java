package org.diplom.blog.api.response;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.diplom.blog.dto.UploadTextError;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadResponse extends CommonResponse<UploadTextError> {

    @Builder
    public UploadResponse(boolean result, UploadTextError errors){
        super(result, errors);
    }
}
