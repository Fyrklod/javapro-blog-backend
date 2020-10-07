package org.diplom.blog.controllers;

import lombok.AllArgsConstructor;
import org.diplom.blog.dto.Mode;
import org.diplom.blog.api.response.PostResponse;
import org.diplom.blog.dto.PostStatus;
import org.diplom.blog.api.request.PostRequest;
import org.diplom.blog.api.request.VoteRequest;
import org.diplom.blog.api.response.CommonResponse;
import org.diplom.blog.api.response.PostListResponse;
import org.diplom.blog.api.response.UploadResponse;
import org.diplom.blog.model.ModerationStatus;
import org.diplom.blog.model.User;
import org.diplom.blog.service.PostService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class ApiPostController {

    private final PostService postService;

    @GetMapping("")
    //@PreAuthorize("hasAuthority('user:reader')")
    public ResponseEntity<PostListResponse> getPosts(@RequestParam(defaultValue = "0") int offset,
                                                     @RequestParam(defaultValue = "50") int limit,
                                                     @RequestParam(defaultValue = "recent") String mode) {

        return postService.getPosts(offset, limit, Mode.fromString(mode));
    }

    @GetMapping("/search")
    public ResponseEntity<PostListResponse> searchPost(@RequestParam(defaultValue = "0") int offset,
                                                       @RequestParam(defaultValue = "50") int limit,
                                                       @RequestParam String query) {

        if (StringUtils.isEmptyOrWhitespace(query)) {
            return postService.getPosts(offset, limit, Mode.RECENT);
        } else {
            return postService.searchPosts(query, offset, limit);
        }

    }

    @GetMapping("/byDate")
    public ResponseEntity<PostListResponse> getPostsByDate(@RequestParam(defaultValue = "0") int offset,
                                                           @RequestParam(defaultValue = "50") int limit,
                                                           @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate date) {

        return postService.getPostsByDate(date, offset, limit);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostListResponse> getPostsByTag(@RequestParam(defaultValue = "0") int offset,
                                                          @RequestParam(defaultValue = "50") int limit,
                                                          @RequestParam String tag) {

        if (StringUtils.isEmptyOrWhitespace(tag)) {
            return postService.getPosts(offset, limit, Mode.RECENT);
        } else {
            return postService.getAllByTag(tag, offset, limit);
        }
    }

    //TODO: Security
    @GetMapping("/moderation")
    public ResponseEntity<PostListResponse> getPostForModeration(@RequestParam(defaultValue = "0") int offset,
                                                                 @RequestParam(defaultValue = "50") int limit,
                                                                 @RequestParam(defaultValue = "new") String moderationStatus) {
        //TODO: заменить параметр User на себя если ты модератор
        User currentUser = new User();
        return postService.getPostForModeration(ModerationStatus.fromString(moderationStatus),
                                                currentUser, offset, limit);
    }

    //TODO: Security
    @GetMapping("/my")
    public ResponseEntity<PostListResponse> getMyPost(@RequestParam(defaultValue = "0") int offset,
                                                      @RequestParam(defaultValue = "50") int limit,
                                                      @RequestParam String postStatus) {
        //TODO: getUser from SecurityContext
        User currentUser = new User();
        return postService.getMyPosts(PostStatus.fromString(postStatus), currentUser, offset, limit);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<PostResponse> getPostById(@PathVariable long id) {
        //TODO:replace to User from SecurityContext
        User user = new User();
        return postService.getPostById(id, user);
    }

    @PostMapping("/")
    public ResponseEntity<UploadResponse> addPost(@RequestBody PostRequest request) {
        //TODO:replace to User from SecurityContext
        User user = new User();
        return  postService.addPost(request, user);
    }

    @PutMapping("/{id}")
    public @ResponseBody ResponseEntity<UploadResponse> editPost(@PathVariable Long id,
                                       @RequestBody PostRequest request) {
        //TODO:replace to User from SecurityContext
        User user = new User();
        return postService.editPost(id, request, user);
    }

    //Security
    //TODO: при подключении Security внести измеения в код (вместо  захарткоженного значения, брать значение пользователя)
    @PostMapping("/like")
    public ResponseEntity<CommonResponse> likePost(@RequestBody VoteRequest request) {
        //TODO:replace to User from SecurityContext
        User user = new User();
        user.setId(1L);
        return postService.savePostVote(request.getPostId(), user, 1);
    }

    //Security
    //TODO: при подключении Security внести измеения в код (вместо  захарткоженного значения, брать значение пользователя)
    @PostMapping("/dislike")
    public ResponseEntity<CommonResponse> dislikePost(@RequestBody VoteRequest request) {
        //TODO:replace to User from SecurityContext
        User user = new User();
        user.setId(1L);
        return postService.savePostVote(request.getPostId(), user, -1);
    }
}
