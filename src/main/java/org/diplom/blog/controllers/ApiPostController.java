package org.diplom.blog.controllers;

import lombok.AllArgsConstructor;
import org.diplom.blog.dto.Mode;
import org.diplom.blog.dto.PostDto;
import org.diplom.blog.dto.PostStatus;
import org.diplom.blog.dto.mapper.PostMapper;
import org.diplom.blog.dto.request.PostRequest;
import org.diplom.blog.dto.request.VoteRequest;
import org.diplom.blog.dto.response.CommonResponse;
import org.diplom.blog.dto.response.PostsResponse;
import org.diplom.blog.dto.response.UploadResponse;
import org.diplom.blog.model.ModerationStatus;
import org.diplom.blog.model.Post;
import org.diplom.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class ApiPostController {

    //@Autowired
    private PostService postService;

    //Security
    @GetMapping("")
    public ResponseEntity<PostsResponse> getPosts(@RequestParam(defaultValue = "0") int offset,
                                                  @RequestParam(defaultValue = "50") int limit,
                                                  @RequestParam(defaultValue = "recent") String mode) {

        int totalPost = 0;
        PostsResponse response = new PostsResponse();

        try {
            Mode modeEnum = Mode.fromString(mode);
            List<Post> posts = null;
            Page<Post> pages = null;

            switch (modeEnum) {
                case EARLY:
                    pages = postService.getAllEarlyPost(offset, limit);
                    break;
                case RECENT:
                    pages = postService.getAllRecentPost(offset, limit);
                    break;
                case BEST:
                    pages = postService.getAllBestPost(offset, limit);
                    break;
                case POPULAR:
                    pages = postService.getAllPopularPost(offset, limit);
                    break;
                default:
                    throw new Exception(String.format("Для %s не описано правило обработки", mode ));
            }

            totalPost = (int)pages.getTotalElements();
            posts = pages.getContent();

            List<PostDto> postDtos = posts.parallelStream()
                    .map(PostMapper::toPostDto)
                    .collect(Collectors.toList());

            //Builder ?-->
            response.setCount(totalPost);
            response.setPosts(postDtos);
            //<---

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(response);
        }

    }

    @PostMapping("/")
    public ResponseEntity<UploadResponse> addPost(@RequestBody PostRequest request) {
        UploadResponse response = new UploadResponse();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<PostDto> getPostById(@PathVariable long id) {
        //TODO:Security..get currentUser
        Post post = postService.getPostById(id);
        if(post == null){
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        PostDto response = PostMapper.toPostDto(post);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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

        int totalPost = 0;
        PostsResponse response = new PostsResponse();

        try {

            Page<Post> pages = StringUtils.isEmptyOrWhitespace(query)  ? postService.getAllRecentPost(offset, limit)
                                                                    : postService.searchPosts(query, offset, limit);

            List<Post> posts = pages.getContent();
            totalPost = (int)pages.getTotalElements();

            List<PostDto> postDtos = posts.parallelStream()
                    .map(PostMapper::toPostDto)
                    .collect(Collectors.toList());

            response.setCount(totalPost);
            response.setPosts(postDtos);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(response);
        }
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

        int totalPost = 0;
        PostsResponse response = new PostsResponse();

        try {

            Page<Post> pages = StringUtils.isEmptyOrWhitespace(tag) ? postService.getAllRecentPost(offset, limit)
                                                                    : postService.getAllByTag(tag, offset, limit);

            List<Post> posts = pages.getContent();
            totalPost = (int)pages.getTotalElements();

            List<PostDto> postDtos = posts.parallelStream()
                    .map(PostMapper::toPostDto)
                    .collect(Collectors.toList());

            response.setCount(totalPost);
            response.setPosts(postDtos);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(response);
        }
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

    //Security
    //TODO: при подключении Security внести измеения в код (вместо  захарткоженного значения, брать значение пользователя)
    @PostMapping("/like")
    public ResponseEntity<CommonResponse> likePost(@RequestBody VoteRequest request) {
        CommonResponse response = new CommonResponse();
        boolean result = postService.like(request.getPostId(), 1);//убрать заглушку
        response.setResult(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //Security
    @PostMapping("/dislike")
    public ResponseEntity<CommonResponse> dislikePost(@RequestBody VoteRequest request) {
        CommonResponse response = new CommonResponse();
        boolean result = postService.dislike(request.getPostId(), 1);//убрать заглушку
        response.setResult(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
