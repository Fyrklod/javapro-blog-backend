package org.diplom.blog.exception;

import org.diplom.blog.dto.AbstractError;
import org.diplom.blog.dto.ImageError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Andrey.Kazakov
 * @date 12.11.2020
 */

@ControllerAdvice
public class CatchExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<AbstractError> handleMaxUploadSizeExceededException(){
        return new ResponseEntity<>(new ImageError("Размер файла превышает допустимый размер")
                , HttpStatus.PAYLOAD_TOO_LARGE);
    }
}
