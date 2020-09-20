package org.diplom.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Andrey.Kazakov
 * @date 15.08.2020
 */
@Getter
@AllArgsConstructor
public class EntityCount<T> {
    T entity;
    Long countRecord;
}
