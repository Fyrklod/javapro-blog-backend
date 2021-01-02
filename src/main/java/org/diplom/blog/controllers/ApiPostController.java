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

    /**
     * Метод getPosts - Список постов.
     * Метод получения постов со всей сопутствующей информацией для главной страницы и подразделов "Новые",
     * "Самые обсуждаемые", "Лучшие" и "Старые". Метод выводит посты, отсортированные в соответствии с
     * параметром mode.
     * GET запрос /api/post
     *
     * @param offset сдвиг от 0 для постраничного вывода.
     * @param limit количество постов, которое надо вывести.
     * @param mode режим вывода (сортировка).
     * @return ResponseEntity<PostListResponse>.
     * @see Mode;
     * @see PostListResponse;
     */
    @GetMapping("")
    public ResponseEntity<PostListResponse> getPosts(@RequestParam(defaultValue = "0") int offset,
                                                     @RequestParam(defaultValue = "50") int limit,
                                                     @RequestParam(defaultValue = "recent") String mode) {

        return postService.getPosts(offset, limit, Mode.fromString(mode));
    }

    /**
     * Метод searchPost - Поиск постов.
     * Метод возвращает посты, соответствующие поисковому запросу - строке query. В случае, если запрос пустой,
     * метод должен выводить все посты.
     * GET запрос /api/post/search
     *
     * @param offset сдвиг от 0 для постраничного вывода.
     * @param limit количество постов, которое надо вывести.
     * @param query поисковый запрос.
     * @return ResponseEntity<PostListResponse>.
     * @see PostListResponse;
     */
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

    /**
     * Метод getPostsByDate - Список постов за указанную дату.
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     * GET запрос /api/post/byDate
     *
     * @param offset сдвиг от 0 для постраничного вывода.
     * @param limit количество постов, которое надо вывести.
     * @param date дата в формате "yyyy-mm-dd".
     * @return ResponseEntity<PostListResponse>.
     * @see PostListResponse;
     */
    @GetMapping("/byDate")
    public ResponseEntity<PostListResponse> getPostsByDate(@RequestParam(defaultValue = "0") int offset,
                                                           @RequestParam(defaultValue = "50") int limit,
                                                           @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate date) {

        return postService.getPostsByDate(date, offset, limit);
    }

    /**
     * Метод getPostsByTag - Список постов по тэгу.
     * Метод выводит список постов, привязанных к тэгу, который был передан методу в качестве параметра tag.
     * GET запрос /api/post/byTag
     *
     * @param offset сдвиг от 0 для постраничного вывода.
     * @param limit количество постов, которое надо вывести.
     * @param tag тэг, по которому нужно вывести все посты.
     * @return ResponseEntity<PostListResponse>.
     * @see PostListResponse;
     */
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

    /**
     * Метод getPostForModeration - Список постов на модерацию.
     * Метод выводит все посты, которые требуют модерационных действий (которые нужно утвердить или отклонить)
     * или над которыми мною были совершены модерационные действия: которые я отклонил или утвердил (это
     * определяется полями moderation_status и moderator_id в таблице posts базы данных).
     * GET запрос /api/post/moderation
     *
     * @param offset сдвиг от 0 для постраничного вывода.
     * @param limit количество постов, которое надо вывести.
     * @param status статус модерации.
     * @return ResponseEntity<PostListResponse>.
     * @see ModerationStatus;
     * @see PostListResponse;
     */
    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:approver')")
    public ResponseEntity<PostListResponse> getPostForModeration(@RequestParam(defaultValue = "0") int offset,
                                                                 @RequestParam(defaultValue = "50") int limit,
                                                                 @RequestParam(defaultValue = "new") String status) {

        return postService.getPostForModeration(ModerationStatus.fromString(status),
                                                offset, limit);
    }

    /**
     * Метод getMyPost - Список моих постов.
     * Метод выводит только те посты, которые создал я (в соответствии с полем user_id в таблице posts базы данных).
     * GET запрос /api/post/my
     *
     * @param offset сдвиг от 0 для постраничного вывода.
     * @param limit количество постов, которое надо вывести.
     * @param status статус модерации.
     * @return ResponseEntity<PostListResponse>.
     * @see PostStatus;
     * @see PostListResponse;
     */
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<PostListResponse> getMyPost(@RequestParam(defaultValue = "0") int offset,
                                                      @RequestParam(defaultValue = "50") int limit,
                                                      @RequestParam String status) {

        return postService.getMyPosts(PostStatus.fromString(status), offset, limit);
    }

    /**
     * Метод getPostById - Получение поста.
     * Метод выводит данные конкретного поста для отображения на странице поста, в том числе,
     * список комментариев и тэгов, привязанных к данному посту.
     * GET запрос /api/post/{id}
     *
     * @param id иденификатор запрашиваемого поста.
     * @return ResponseEntity<PostResponse>.
     * @see PostResponse;
     */
    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<PostResponse> getPostById(@PathVariable long id) {
        return postService.getPostById(id);
    }

    /**
     * Метод addPost - Добавление поста.
     * Метод отправляет данные поста, которые пользователь ввёл в форму публикации.
     * POST запрос /api/post/login
     *
     * @param postRequest тело запроса в формате Json.
     * @return ResponseEntity<UploadResponse>.
     * @see PostRequest;
     * @see UploadResponse;
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<UploadResponse> addPost(@RequestBody PostRequest postRequest) throws Exception {
        return  postService.addPost(postRequest);
    }

    /**
     * Метод editPost - Редактирование поста.
     * Метод изменяет данные поста с идентификатором ID на те, которые пользователь ввёл в форму публикации.
     * PUT запрос /api/post/{id}
     *
     * @param id идентификатор поста.
     * @param postRequest тело запроса в формате Json.
     * @return ResponseEntity<UploadResponse>.
     * @see PostRequest;
     * @see UploadResponse;
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:writer')")
    public @ResponseBody ResponseEntity<UploadResponse> editPost(@PathVariable Long id,
                                       @RequestBody PostRequest postRequest) throws Exception {
        return postService.editPost(id, postRequest);
    }

    /**
     * Метод likePost - Лайк поста.
     * Метод сохраняет в таблицу post_votes лайк текущего авторизованного пользователя.
     * В случае повторного лайка возвращает {result: false}.
     * POST запрос /api/post/like
     *
     * @param voteRequest тело запроса в формате Json.
     * @return ResponseEntity<SimpleResponse>.
     * @see VoteRequest;
     * @see SimpleResponse;
     */
    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<SimpleResponse> likePost(@RequestBody VoteRequest voteRequest) {
        return postService.savePostVote(voteRequest.getPostId(), 1);
    }

    /**
     * Метод dislikePost - Дизлайк поста.
     * Метод сохраняет в таблицу post_votes дизлайк текущего авторизованного пользователя.
     * В случае повторного дизлайка возвращает {result: false}.
     * POST запрос /api/post/dislike
     *
     * @param voteRequest тело запроса в формате Json.
     * @return ResponseEntity<SimpleResponse>.
     * @see VoteRequest;
     * @see SimpleResponse;
     */
    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:writer')")
    public ResponseEntity<SimpleResponse> dislikePost(@RequestBody VoteRequest voteRequest) {
        return postService.savePostVote(voteRequest.getPostId(), -1);
    }
}
