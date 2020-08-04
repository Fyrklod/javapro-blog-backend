package org.diplom.blog.dto.mapper;

import org.diplom.blog.dto.model.UserDto;
import org.diplom.blog.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto()
                .setId(user.getId())
                .setName(user.getFullName())
                .setPhoto(user.getPhoto())
                //TODO:откуда?
                .setRemovePhoto(0)
                .setEmail(user.getEmail())
                .setModeration(user.isModerator())
                //TODO:откуда?
                .setModerationCount(0)
                //TODO:откуда?
                .setSettings(false);
    }
}
