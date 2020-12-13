package org.diplom.blog.exception;

import org.diplom.blog.dto.UploadTextError;

/**
 * @author Andrey.Kazakov
 * @date 29.11.2020
 */
public class UploadTextException extends AbstractErrorException {
    public UploadTextException(UploadTextError errors) {
        super(errors);
    }
}
