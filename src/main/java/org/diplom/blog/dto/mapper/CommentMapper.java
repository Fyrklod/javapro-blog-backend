package org.diplom.blog.dto.mapper;


import org.diplom.blog.dto.CommentDto;
import org.diplom.blog.model.PostComment;


public class CommentMapper {
    public static CommentDto toCommentDto(PostComment comment) {
        return new CommentDto()
                .setId(comment.getId())
                .setText(comment.getText())
                .setTimestamp(comment.getTime().getTime())
                .setUser(UserMapper.toUserDto(comment.getAuthor()));
    }
}
