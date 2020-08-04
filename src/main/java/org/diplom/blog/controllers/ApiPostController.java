package org.diplom.blog.controllers;

import org.diplom.blog.dto.model.PostDto;
import org.diplom.blog.dto.request.PostRequest;
import org.diplom.blog.dto.response.PostsResponse;
import org.diplom.blog.dto.response.UploadResponse;
import org.diplom.blog.model.Mode;
import org.diplom.blog.model.ModerationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    //Security
    @GetMapping("")
    public ResponseEntity<PostsResponse> getPosts(@RequestParam(defaultValue = "0") int offset,
                                                  @RequestParam(defaultValue = "50") int limit,
                                                  @RequestParam(defaultValue = "recent") Mode mode) {
        PostsResponse response = new PostsResponse();
        response.setCount(0);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/")
    public ResponseEntity<UploadResponse> addPost(@RequestBody PostRequest request) {
        UploadResponse response = new UploadResponse();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public @ResponseBody PostDto getPostById(@PathVariable int id) {
        PostDto post = new PostDto();

        return post;
    }

    @PutMapping("/{id}")
    public @ResponseBody PostDto editPost(@PathVariable int id,
                                       @RequestBody PostRequest request) {
        PostDto post = new PostDto();

        return post;
    }

    @GetMapping("/search")
    public ResponseEntity<PostsResponse> searchPost(@RequestParam(defaultValue = "0") int offset,
                                                    @RequestParam(defaultValue = "50") int limit,
                                                    @RequestParam String query) {

        PostsResponse response = new PostsResponse();
        response.setCount(0);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/byDate")
    public ResponseEntity<PostsResponse> getPostsByDate(@RequestParam(defaultValue = "0") int offset,
                                                        @RequestParam(defaultValue = "50") int limit,
                                                        Date date) {

        PostsResponse response = new PostsResponse();
        response.setCount(0);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostsResponse> getPostsByTag(@RequestParam(defaultValue = "0") int offset,
                                        @RequestParam(defaultValue = "50") int limit,
                                        @RequestParam String tag) {

        PostsResponse response = new PostsResponse();
        response.setCount(0);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/moderation")
    public ResponseEntity<PostsResponse> getPostForModeration(@RequestParam(defaultValue = "0") int offset,
                                               @RequestParam(defaultValue = "50") int limit,
                                               @RequestParam ModerationStatus status) {

        PostsResponse response = new PostsResponse();
        response.setCount(0);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<PostsResponse> getMyPost(@RequestParam(defaultValue = "0") int offset,
                                    @RequestParam(defaultValue = "50") int limit,
                                    @RequestParam ModerationStatus status) {

        PostsResponse response = new PostsResponse();
        response.setCount(0);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/like")
    public boolean likePost(@RequestParam(name = "post_id") Integer postId) {

        return false;
    }

    @PostMapping("/dislike")
    public boolean dislikePost(@RequestParam(name = "post_id") Integer postId) {

        return false;
    }
}
