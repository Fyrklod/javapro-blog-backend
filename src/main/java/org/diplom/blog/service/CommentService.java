package org.diplom.blog.service;

import lombok.AllArgsConstructor;
import org.diplom.blog.api.request.CommentRequest;
import org.diplom.blog.api.response.CommentResponse;
import org.diplom.blog.dto.Error;
import org.diplom.blog.model.Post;
import org.diplom.blog.model.PostComment;
import org.diplom.blog.repository.CommentRepository;
import org.diplom.blog.repository.PostRepository;
import org.diplom.blog.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    //TODO: доработать вместе с репозиторием
    public ResponseEntity<CommentResponse> addComment(CommentRequest commentRequest) {
        CommentResponse response = new CommentResponse();
        try {
            if(commentRequest.getText().length() < 1){
                throw new Exception("Комментарий должен содержать тест");
            }

            Post post = postRepository.findById(commentRequest.getPostId())
                    .orElseThrow(() -> new Exception("Пост не найден"));

            PostComment parentComment = commentRequest.getParentId() != null
                                         ? commentRepository.findById(commentRequest.getParentId())
                                            .orElseThrow(() -> new Exception("Комментарий не найден"))
                                         : null;

            PostComment comment = PostComment.builder()
                                            .parent(parentComment)
                                            .post(post)
                                            .text(commentRequest.getText())
                                            .author(userService.getCurrentUser())
                                            .build();

            comment = commentRepository.save(comment);
            response.setId(comment.getId());
            response.setResult(true);
        } catch(Exception ex) {
            Error error = new Error();
            error.setText(ex.getMessage());
            response.setErrors(error);
            response.setResult(false);
        }

        return ResponseEntity.ok(response);
    }
}
