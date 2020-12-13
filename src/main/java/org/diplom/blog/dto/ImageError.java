package org.diplom.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Andrey.Kazakov
 * @date 16.11.2020
 */
@Getter
@AllArgsConstructor
public class ImageError extends AbstractError {
    private final String image;
}
