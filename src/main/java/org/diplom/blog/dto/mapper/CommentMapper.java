package org.diplom.blog.dto.mapper;

import org.diplom.blog.dto.CommentDto;
import org.diplom.blog.model.PostComment;
import org.diplom.blog.utils.DateUtil;

public class CommentMapper {
    public static CommentDto toCommentDto(PostComment comment) {
        return CommentDto.builder()
                    .id(comment.getId())
                    .text(comment.getText())
                    .timestamp(DateUtil.getTimestampFromLocalDateTime(comment.getTime()))
                    .user(UserMapper.toUserExtInfo(comment.getAuthor()))
                    .build();
    }
}
