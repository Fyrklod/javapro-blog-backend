package org.diplom.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadTextError extends AbstractError{
    private String title;
    private String text;
}
