package org.diplom.blog.dto.mapper;


import org.diplom.blog.dto.CommentDto;
import org.diplom.blog.model.PostComment;
import org.diplom.blog.utils.DateUtil;

import java.sql.Timestamp;


public class CommentMapper {
    public static CommentDto toCommentDto(PostComment comment) {
        return new CommentDto()
                .setId(comment.getId())
                .setText(comment.getText())
                .setTimestamp(DateUtil.getTimestampFromLocalDateTime(comment.getTime()))
                .setUser(UserMapper.toUserDto(comment.getAuthor()));
    }
}
