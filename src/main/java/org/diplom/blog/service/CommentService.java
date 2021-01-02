package org.diplom.blog.service;

import lombok.SneakyThrows;
import org.diplom.blog.api.request.CommentRequest;
import org.diplom.blog.api.response.CommentResponse;
import org.diplom.blog.dto.UploadTextError;
import org.diplom.blog.exception.UploadTextException;
import org.diplom.blog.model.Post;
import org.diplom.blog.model.PostComment;
import org.diplom.blog.repository.CommentRepository;
import org.diplom.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;

/**
 * @author Andrey.Kazakov
 * @date 16.09.2020
 */
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserService userService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    /**
     * Метод addComment.
     * Добавление нового комментария.
     *
     * @param commentRequest тело запроса в формате Json.
     * @return ResponseEntity<CommentResponse> .
     * @see CommentRequest;
     * @see CommentResponse;
     */
    @SneakyThrows
    public ResponseEntity<CommentResponse> addComment(CommentRequest commentRequest) {

        if(commentRequest.getText().length() < 1) {
            UploadTextError error = new UploadTextError();
            error.setText("Текст комментария не задан или слишком короткий");

            throw new UploadTextException(error);
        }

        Post post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new InvalidParameterException("Пост не найден"));

        PostComment parentComment = commentRequest.getParentId() != null
                ? commentRepository.findById(commentRequest.getParentId())
                    .orElseThrow(() -> new InvalidParameterException("Комментарий не найден"))
                : null;

        PostComment comment = PostComment.builder()
                .parent(parentComment)
                .post(post)
                .text(commentRequest.getText())
                .author(userService.getCurrentUser())
                .build();

        comment = commentRepository.save(comment);
        return ResponseEntity.ok(
                new CommentResponse(comment.getId())
            );
    }
}
