package org.diplom.blog.exception;

import org.diplom.blog.dto.ImageError;

/**
 * @author Andrey.Kazakov
 * @date 29.11.2020
 */
public class UploadImageException extends AbstractErrorException {
    public UploadImageException(ImageError errors) {
        super(errors);
    }
}
