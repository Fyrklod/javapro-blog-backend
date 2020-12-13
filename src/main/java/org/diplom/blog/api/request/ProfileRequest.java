package org.diplom.blog.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Andrey.Kazakov
 * @date 28.11.2020
 */
@Getter
@Builder
@AllArgsConstructor
public class ProfileRequest {
    private final String name;
    private final String email;
    private final String password;
    private final String pathPhoto;
    private final int removePhoto;
}
