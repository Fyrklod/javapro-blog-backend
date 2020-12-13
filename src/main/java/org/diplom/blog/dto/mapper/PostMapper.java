package org.diplom.blog.dto.mapper;

import org.diplom.blog.api.response.PostResponse;
import org.diplom.blog.model.Post;
import org.diplom.blog.model.Tag;
import org.diplom.blog.utils.DateUtil;

import java.util.stream.Collectors;

public class PostMapper {

    public static PostResponse toPostForListResponse(Post post) {
        int countOfLikes = (int)post.getVotes().stream()
                .filter(v -> v.getValue() > 0)
                .count();
        int countOfDislikes = (int)post.getVotes().stream()
                .filter(v -> v.getValue() < 0)
                .count();
        String announce = post.getText().replaceAll("\\<.*?\\>", "");

        return PostResponse.builder()
                .id(post.getId())
                .timestamp(DateUtil.getTimestampFromLocalDateTime(post.getDate()))
                .title(post.getTitle())
                .announce( announce.length() > 100
                                    ? announce.substring(0, 100) + "..."
                                    : announce)
                .likeCount(countOfLikes)
                .dislikeCount(countOfDislikes)
                .commentCount(post.getPostComments().size())
                .viewCount(post.getViewCount())
                .user(UserMapper.toUserBasicInfo(post.getAuthor()))
                .build();
    }

    public static PostResponse toSinglePostResponse(Post post) {
        int countOfLikes = (int)post.getVotes().stream()
                .filter(v -> v.getValue() > 0)
                .count();
        int countOfDislikes = (int)post.getVotes().stream()
                .filter(v -> v.getValue() < 0)
                .count();

        return PostResponse.builder()
                .id(post.getId())
                .timestamp(DateUtil.getTimestampFromLocalDateTime(post.getDate()))
                .isActive(post.isActive())
                .user(UserMapper.toUserBasicInfo(post.getAuthor()))
                .title(post.getTitle())
                .text(post.getText())
                .likeCount(countOfLikes)
                .dislikeCount(countOfDislikes)
                .viewCount(post.getViewCount())
                .comments(post.getPostComments()
                        .stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()))
                .tags(post.getTags()
                        .stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}
