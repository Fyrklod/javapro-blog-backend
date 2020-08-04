package org.diplom.blog.dto.mapper;

import org.diplom.blog.dto.model.TagDto;
import org.diplom.blog.dto.model.UserDto;
import org.diplom.blog.model.Tag;
import org.diplom.blog.model.User;

public class TagMapper {
    public static TagDto toTagDto(Tag tag) {
        return new TagDto()
                .setName(tag.getName())
                //TODO:реализовать метод подсчета веса
                .setWeight(0);
    }
}
