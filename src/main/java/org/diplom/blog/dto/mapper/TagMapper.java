package org.diplom.blog.dto.mapper;

import org.diplom.blog.dto.EntityCount;
import org.diplom.blog.dto.TagDto;
import org.diplom.blog.model.Tag;

public class TagMapper {
    public static TagDto toTagDto(EntityCount<Tag> tagCount, int commonCount) {
        return new TagDto(tagCount.getEntity().getName(),
                (double) tagCount.getCountRecord() / commonCount);
    }
}
