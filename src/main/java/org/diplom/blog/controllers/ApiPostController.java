package org.diplom.blog.controllers;

import org.diplom.blog.dto.model.Post;
import org.diplom.blog.dto.request.PostRequest;
import org.diplom.blog.dto.response.PostsResponse;
import org.diplom.blog.dto.response.UploadResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    @GetMapping("")
    public ResponseEntity<PostsResponse> getPosts(@RequestParam(defaultValue = "0") int offset,
                                                  @RequestParam(defaultValue = "50") int limit,
                                                  @RequestParam(defaultValue = "recent") String mode) {
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
    public @ResponseBody Post getPostById(@PathVariable int id) {
        Post post = new Post();

        return post;
    }

    @PutMapping("/{id}")
    public @ResponseBody Post editPost(@PathVariable int id,
                                       @RequestBody PostRequest request) {
        Post post = new Post();

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
                                               @RequestParam String status) {

        PostsResponse response = new PostsResponse();
        response.setCount(0);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<PostsResponse> getMyPost(@RequestParam(defaultValue = "0") int offset,
                                    @RequestParam(defaultValue = "50") int limit,
                                    @RequestParam String status) {

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
