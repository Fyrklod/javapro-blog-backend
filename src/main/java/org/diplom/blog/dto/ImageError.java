package org.diplom.blog.dto;

import lombok.AllArgsConstructor;

/**
 * @author Andrey.Kazakov
 * @date 16.11.2020
 */
@AllArgsConstructor
public class ImageError extends AbstractError {
    private final String image;
}
