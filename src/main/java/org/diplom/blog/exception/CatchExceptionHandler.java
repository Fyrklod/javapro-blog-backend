package org.diplom.blog.exception;

import org.diplom.blog.api.response.CommonResponse;
import org.diplom.blog.api.response.ImageResponse;
import org.diplom.blog.api.response.UploadResponse;
import org.diplom.blog.dto.AbstractError;
import org.diplom.blog.dto.ImageError;
import org.diplom.blog.dto.UploadTextError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.InvalidParameterException;

/**
 * @author Andrey.Kazakov
 * @date 12.11.2020
 */

@ControllerAdvice
public class CatchExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ImageResponse> handleMaxUploadSizeExceededException(){
        return ResponseEntity.badRequest().body(
                new ImageResponse(false,
                        new ImageError("Размер файла превышает допустимый размер")
                )
        );
    }

    @ExceptionHandler(UploadImageException.class)
    protected ResponseEntity<ImageResponse> handleUploadImageException(UploadImageException ex){
        return ResponseEntity.badRequest().body(
                new ImageResponse(false,
                        new ImageError(ex.getMessage())
                )
        );
    }

    @ExceptionHandler(UploadTextException.class)
    protected ResponseEntity<UploadResponse> handleUploadTextException(UploadTextException ex) {
        return ResponseEntity.ok(new UploadResponse(false, (UploadTextError) ex.getErrors()));
    }

    @ExceptionHandler(InvalidParameterException.class)
    protected ResponseEntity<String> handleInvalidParameterException(InvalidParameterException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
