package org.diplom.blog.dto.mapper;

import org.diplom.blog.dto.UserInfo;
import org.diplom.blog.model.User;

public class UserMapper {
    public static UserInfo toUserFullInfo(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .name(user.getFullName())
                .photo(user.getPhoto())
                .email(user.getEmail())
                .moderation(user.isModerator())
                .moderationCount(0L)
                .settings(user.isModerator())
                .build();
    }

    public static UserInfo toUserExtInfo(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .name(user.getFullName())
                .photo(user.getPhoto())
                .build();
    }

    public static UserInfo toUserBasicInfo(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .name(user.getFullName())
                .build();
    }
}
