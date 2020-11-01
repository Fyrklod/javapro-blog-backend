package org.diplom.blog.dto;

import lombok.SneakyThrows;
import org.diplom.blog.model.ModerationStatus;

/**
 * @author Andrey.Kazakov
 * @date 10.08.2020
 */
public enum PostStatus {
    INACTIVE,
    PENDING,
    DECLINED,
    PUBLISHED;

    @SneakyThrows
    public static PostStatus fromString(String status){
        switch (status) {
            case "pending": return PENDING;
            case "declined": return DECLINED;
            case "published": return PUBLISHED;
            case "inactive": return INACTIVE;
            default:
                throw new Exception("Данного значения не существует");
        }
    }
}
