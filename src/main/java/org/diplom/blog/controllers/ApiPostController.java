package org.diplom.blog.controllers;

import lombok.AllArgsConstructor;
import org.diplom.blog.api.response.*;
import org.diplom.blog.dto.Mode;
import org.diplom.blog.dto.PostStatus;
import org.diplom.blog.api.request.PostRequest;
import org.diplom.blog.api.request.VoteRequest;
import org.diplom.blog.model.ModerationStatus;
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

    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:approver')")
    public ResponseEntity<PostListResponse> getPostForModeration(@RequestParam(defaultValue = "0") int offset,
                                                                 @RequestParam(defaultValue = "50") int limit,
                                                                 @RequestParam(defaultValue = "new") String status) {

        return postService.getPostForModeration(ModerationStatus.fromString(status),
                                                offset, limit);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<PostListResponse> getMyPost(@RequestParam(defaultValue = "0") int offset,
                                                      @RequestParam(defaultValue = "50") int limit,
                                                      @RequestParam String status) {

        return postService.getMyPosts(PostStatus.fromString(status), offset, limit);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<PostResponse> getPostById(@PathVariable long id) {
        return postService.getPostById(id);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<UploadResponse> addPost(@RequestBody PostRequest request) throws Exception {
        return  postService.addPost(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:writer')")
    public @ResponseBody ResponseEntity<UploadResponse> editPost(@PathVariable Long id,
                                       @RequestBody PostRequest request) throws Exception {
        return postService.editPost(id, request);
    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<SimpleResponse> likePost(@RequestBody VoteRequest request) {
        return postService.savePostVote(request.getPostId(), 1);
    }

    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<SimpleResponse> dislikePost(@RequestBody VoteRequest request) {
        return postService.savePostVote(request.getPostId(), -1);
    }
}
