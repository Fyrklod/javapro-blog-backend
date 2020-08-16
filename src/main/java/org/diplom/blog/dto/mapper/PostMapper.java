package org.diplom.blog.dto.mapper;

import org.diplom.blog.dto.PostDto;
import org.diplom.blog.model.Post;
import org.diplom.blog.model.Tag;

import java.util.stream.Collectors;

public class PostMapper {
    public static PostDto toPostDto(Post post) {
        int countOfLikes = (int)post.getVotes().stream()
                                                .filter(v -> v.getValue() > 0)
                                                .count();
        int countOfDislikes = (int)post.getVotes().stream()
                                                    .filter(v -> v.getValue() < 0)
                                                    .count();

        return new PostDto()
                .setId(post.getId())
                .setTimestamp(post.getDate().getTime())
                .setIsActive(post.isActive())
                .setUser(UserMapper.toUserDto(post.getAuthor()))
                .setTitle(post.getTitle())
                .setText(post.getText())
                .setLikeCount(countOfLikes)
                .setDislikeCount(countOfDislikes)
                .setCommentCount(post.getPostComments().size())
                .setViewCount(post.getViewCount())
                .setComments(post.getPostComments().stream()
                                                    .map(CommentMapper::toCommentDto)
                                                    .collect(Collectors.toList()))
                .setTags(post.getTags().stream()
                                        .map(Tag::getName)
                                        .collect(Collectors.toList()));
    }
}
