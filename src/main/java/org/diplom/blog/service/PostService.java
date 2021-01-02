package org.diplom.blog.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.diplom.blog.api.response.*;
import org.diplom.blog.dto.UploadTextError;
import org.diplom.blog.dto.Mode;
import org.diplom.blog.dto.PostStatus;
import org.diplom.blog.dto.mapper.PostMapper;
import org.diplom.blog.api.request.PostRequest;
import org.diplom.blog.model.*;
import org.diplom.blog.repository.PostRepository;
import org.diplom.blog.repository.PostVoteRepository;
import org.diplom.blog.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final TagService tagService;
    private final SettingService settingService;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostVoteRepository postVoteRepository,
                       TagService tagService,
                       SettingService settingService,
                       UserService userService) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.tagService = tagService;
        this.settingService = settingService;
        this.userService = userService;
    }

    /**
     * Метод getPosts - Получить список постов, согласно указанного режима фильтра.
     * @param offset - смещение в общем списке постов.
     * @param limit - лимитированное количество постов.
     * @param mode - режим фильтра.
     * @return ResponseEntity<PostListResponse>.
     * @see Mode;
     * @see PostListResponse;
     */
    public ResponseEntity<PostListResponse> getPosts(int offset, int limit, Mode mode) {
        Page<Post> pages;

        try {
            switch (mode) {
                case EARLY:
                    pages = getAllEarlyPost(offset, limit);
                    break;
                case RECENT:
                    pages = getAllRecentPost(offset, limit);
                    break;
                case BEST:
                    pages = getAllBestPost(offset, limit);
                    break;
                case POPULAR:
                    pages = getAllPopularPost(offset, limit);
                    break;
                default:
                    throw new Exception(String.format("Для %s не описано правило обработки", mode ));
            }

            return preparePostsResponse(pages);
        } catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Метод searchPosts - Получить список постов, которые содержат в себе данные указанные в "шаблоне поиска".
     * @param pattern - шаблон поиска.
     * @param pageIndex - номер открываемой страницы.
     * @param pageSize - количество постов на странице.
     * @return ResponseEntity<PostListResponse>.
     * @see PostListResponse;
     */
    public ResponseEntity<PostListResponse> searchPosts(String pattern, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);

        try {
            Page<Post> pages =  postRepository.searchAllByPattern(pattern,
                    ModerationStatus.ACCEPTED.toString(),
                    true,
                    LocalDateTime.now(),
                    pageable
            );

            return preparePostsResponse(pages);
        } catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Метод getAllByTag - Получить список постов по указанному тегу.
     * @param tag - тэг.
     * @param pageIndex - номер открываемой страницы.
     * @param pageSize - количество постов на странице.
     * @return ResponseEntity<PostListResponse>.
     * @see PostListResponse;
     */
    public ResponseEntity<PostListResponse> getAllByTag(String tag, int pageIndex, int pageSize){
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);

        try {
            Page<Post> pages = postRepository.findAllByTagName(tag,
                    ModerationStatus.ACCEPTED.toString(),
                    true,
                    LocalDateTime.now(),
                    pageable
            );

            return preparePostsResponse(pages);
        } catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Метод getPostsByDate - Получить список постов за указанную дату.
     * @param localDate - дата за которую нужно показать список постов.
     * @param pageIndex - номер открываемой страницы.
     * @param pageSize - количество постов на странице.
     * @return ResponseEntity<PostListResponse>.
     * @see PostListResponse;
     */
    public ResponseEntity<PostListResponse> getPostsByDate(LocalDate localDate, int pageIndex, int pageSize){
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);

        try {
            Page<Post> pages = postRepository.findPublishPostByDate(ModerationStatus.ACCEPTED.toString(),
                                                        true,  localDate, pageable);

            return preparePostsResponse(pages);
        } catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Метод getPostForModeration - Список постов для модерации.
     * @param moderationStatus - статус модерации.
     * @param pageIndex - номер открываемой страницы.
     * @param pageSize - количество постов на странице.
     * @return ResponseEntity<PostListResponse>.
     * @see ModerationStatus;
     * @see PostListResponse;
     */
    public ResponseEntity<PostListResponse> getPostForModeration(ModerationStatus moderationStatus,
                                                                 int pageIndex, int pageSize){
        try {
            User currentUser = userService.getCurrentUser();

            if(!currentUser.isModerator()){
                return ResponseEntity.status(HttpStatus.LOCKED).body(null);
            }

            Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);
            Page<Post> pages = moderationStatus.equals(ModerationStatus.NEW)
                           ? postRepository.findByModerationStatusValueAndIsActive(moderationStatus.toString(),
                                                                             true, pageable)
                           : postRepository.findByModeratorAndModerationStatusValueAndIsActive(currentUser
                                                                            , moderationStatus.toString(), true, pageable);
            return preparePostsResponse(pages);

        } catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Метод getMyPosts - Список моих постов.
     * @param postStatus - статус постов.
     * @param pageIndex - номер открываемой страницы.
     * @param pageSize - количество постов на странице.
     * @return ResponseEntity<PostListResponse>.
     * @see PostStatus;
     * @see PostListResponse;
     */
    @SneakyThrows
    public ResponseEntity<PostListResponse> getMyPosts(PostStatus postStatus, int pageIndex, int pageSize) {
        User currentUser = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);
        Page<Post> pages = postStatus.equals(PostStatus.INACTIVE)
                ? postRepository.findByAuthorAndIsActive(currentUser, false, pageable)
                : postRepository.findByAuthorAndModerationStatusValueAndIsActive(currentUser,
                                        ModerationStatus.fromPostStatus(postStatus).toString(),
                                        true, pageable);

        return preparePostsResponse(pages);
    }

    /**
     * Метод getPostById - Запрос на получение поста по его идентификатору.
     * @param id - идентификатор запрашиваемого поста.
     * @return ResponseEntity<PostResponse>.
     * @see PostResponse;
     */
    @Transactional
    public ResponseEntity<PostResponse> getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow();

        if(!tryToOpenPost(post)) {
            return ResponseEntity.notFound().build();
        }

        increaseOfViewsPost(post);
        PostResponse response = PostMapper.toSinglePostResponse(post);
        return ResponseEntity.ok(response);
    }

    /**
     * Метод addPost - добавление нового поста.
     * @param postRequest - запрос на добавление поста.
     * @return ResponseEntity<UploadResponse>.
     * @see PostRequest;
     * @see UploadResponse;
     */
    public ResponseEntity<UploadResponse> addPost(PostRequest postRequest) throws Exception {
        return savePost(0L, postRequest);
    }

    /**
     * Метод editPost - редактирование поста.
     * @param id - идентификатор редактируемого поста.
     * @param postRequest - запрос на изменение постов.
     * @return ResponseEntity<UploadResponse>.
     * @see PostRequest;
     * @see UploadResponse;
     */
    public ResponseEntity<UploadResponse> editPost(Long id, PostRequest postRequest) throws Exception{
        return savePost(id, postRequest);
    }

    /**
     * Метод savePostVote - сохранение оценки поста (лайка или дизлайка).
     * @param postId - id поста.
     * @param value - значение оценки (1 - лайк, -1 - дизлайк).
     * @return ResponseEntity<SimpleResponse>.
     * @see SimpleResponse;
     */
    public ResponseEntity<SimpleResponse> savePostVote(Long postId, int value) {
        User user = userService.getCurrentUser();

        boolean result = true;
        PostVote postVote;

        try {
            Optional<PostVote> optionalPostVote =  postVoteRepository.findByPostIdAndUserId(postId, user.getId());
            if(optionalPostVote.isPresent())
            {
                postVote = optionalPostVote.get();
                if(postVote.getValue().equals(value)){
                    return ResponseEntity.ok(new SimpleResponse(false));
                } else {
                    postVote.setValue(value);
                }
            } else {
                postVote = new PostVote(postId, user.getId(), value);
            }
            postVoteRepository.save(postVote);

        } catch (Exception ex){
            ex.printStackTrace();
            result = false;
        }

        return ResponseEntity.ok(new SimpleResponse(result));
    }

    /**
     * Метод increaseOfViewsPost - увеличение просмотра поста.
     * Увеличение количества просмотров производится в случае, если пост просмотрен только авторизованным
     * пользователем, но если этот пользователь не является ни модератором и ни автором данного поста.
     * @param post - пост который пытаются открыть.
     * @see Post;
     */
    @Transactional
    private void increaseOfViewsPost(Post post) {
        if(post == null) {
            return;
        }

        try {
            User reader = userService.getCurrentUser();

            if(!post.getAuthor().equals(reader) && !reader.isModerator()) {
                postRepository.incrementViewCountOfPost(post.getId());
            }
        } catch (AuthenticationException access) {
            //не авторизованноу пользователю разрешено смотреть пост, но его просмотр не будет посчитан
        } catch (Exception ex) {
            log.info("При просмотре поста id={} получена ошибка {}", post.getId(), ex.getMessage());
        }
    }

    /**
     * Метод savePost - Сохранение постов.
     * если включен этот режим, то все новые посты пользователей с moderation = false обязательно должны попадать
     * на модерацию, у постов при создании должен быть установлен moderation_status = NEW. Eсли значения
     * POST_PREMODERATION = false (режим премодерации выключен), то все новые посты должны сразу публиковаться
     * (если у них установлен параметр active = 1), у постов при создании должен быть установлен
     * moderation_status = ACCEPTED.
     * @param id - идентификатор поста (если производится добавление нового, то -1 , иначе id из базы).
     * @param postRequest - запрос за изменение поста.
     * @return ResponseEntity<UploadResponse> - возврат true, если возможно открыть иначе false .
     * @see UploadResponse;
     * @see PostRequest;
     */
    @Transactional
    private ResponseEntity<UploadResponse> savePost(Long id, PostRequest postRequest) throws Exception {
        UploadResponse response;
        Boolean postPremoderation = settingService.getBooleanSettingValueByCode("POST_PREMODERATION");

        if(postRequest.getTitle().length() > 3 && postRequest.getText().length() > 50) {
            List<Tag> tags = tagService.saveTagByListName(Arrays.asList(postRequest.getTags()));
            LocalDateTime postDateTime = DateUtil.getLocalDateTimeFromTimestamp(postRequest.getTimestamp());

            Post post = ( id > 0 ) ? postRepository.getOne(id)
                                   : new Post();
            User currentUser = userService.getCurrentUser();

            if(id == 0 ) {
                post.setViewCount(0);
                post.setAuthor(currentUser);
            }

            post.setTitle(postRequest.getTitle())
                .setText(postRequest.getText())
                .setTags(tags)
                .setActive(postRequest.isActive())
                .setDate(
                       postDateTime.isBefore(LocalDateTime.now())
                                ? postDateTime
                                : LocalDateTime.now()
                )
                .setModerationStatusValue(
                        currentUser.equals(post.getAuthor())            //Если добавляет или изменяет автор
                                ? postPremoderation                     //  и включен режим премодерации
                                    ? ModerationStatus.NEW.toString()              //      устанавливается статус NEW
                                    : ModerationStatus.ACCEPTED.toString()         //      иначе ACCEPTED
                                :  post.getModerationStatusValue()           //Иначе (Если изменения сделал модератор)
                                                                        //      статус остается прежним
                );

            postRepository.save(post);

            response = UploadResponse.builder()
                                     .result(true)
                                     .build();
        } else {
            UploadTextError error = new UploadTextError();
            if(postRequest.getTitle().length() <= 3){
                error.setTitle("Заголовок не установлен");
            }

            if(postRequest.getText().length() <= 50){
                error.setText("Текст публикации слишком короткий");
            }

            response = UploadResponse.builder()
                                    .result(false)
                                    .errors(error).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Метод preparePostsResponse - Подготовка сервисного ответа со сиском постов.
     * @param pages - страница постов.
     * @return ResponseEntity<PostListResponse> - список постов.
     * @see Post;
     * @see PostListResponse;
     */
    private ResponseEntity<PostListResponse> preparePostsResponse(Page<Post> pages){
        try {
            Long totalPost = pages.getTotalElements();
            List<Post> posts = pages.getContent();

            List<PostResponse> postResponseList = posts.stream()
                    .map(PostMapper::toPostForListResponse)
                    .collect(Collectors.toList());

            PostListResponse response = PostListResponse.builder()
                    .count(totalPost)
                    .posts(postResponseList)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Метод getAllEarlyPost - Получить все посты отсортированные по дате
     * (Пост созданный первым выводится первым).
     * @param pageIndex - номер открываемой страницы.
     * @param pageSize - количество постов на отображаемых страницах.
     * @return Page<Post> - отсортированная страница постов.
     * @see Post;
     */
    private Page<Post> getAllEarlyPost(int pageIndex, int pageSize){
        return getAllOrderedPostByCurrentDate(pageIndex, pageSize, Sort.Direction.ASC);
    }

    /**
     * Метод getAllRecentPost - Получить все посты отсортированные по дате
     * (Пост созданный последним выводится первым).
     * @param pageIndex - номер открываемой страницы.
     * @param pageSize - количество постов на отображаемых страницах.
     * @return Page<Post> - отсортированная страница постов.
     * @see Post;
     */
    private Page<Post> getAllRecentPost(int pageIndex, int pageSize){
        return getAllOrderedPostByCurrentDate(pageIndex, pageSize, Sort.Direction.DESC);
    }

    /**
     * Метод getAllPopularPost - Получить все посты отсортированные по количеству комментариев
     * (самые обсуждаемые будут первыми).
     * @param pageIndex - номер открываемой страницы.
     * @param pageSize - количество постов на отображаемых страницах.
     * @return Page<Post> - отсортированная страница постов.
     * @see Post;
     */
    private Page<Post> getAllPopularPost(int pageIndex, int pageSize){
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);

        return postRepository.findAllWithCountOfCommentsOrderByCountDesc(
                ModerationStatus.ACCEPTED.toString(),
                true,
                LocalDateTime.now(),
                pageable
        );
    }

    /**
     * Метод getAllBestPost - Получить все посты отсортированные по количеству лайков (лучшие будут первыми).
     * @param pageIndex - номер открываемой страницы.
     * @param pageSize - кколичество постов на отображаемых страницах.
     * @return Page<Post> - отсортированную страницу постов .
     * @see Post;
     */
    private Page<Post> getAllBestPost(int pageIndex, int pageSize){
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);
        return postRepository.findAllWithCountOfVotesOrderByCountDesc(
                ModerationStatus.ACCEPTED.toString(),
                true,
                LocalDateTime.now(),
                pageable
        );
    }

    /**
     * Метод getAllOrderedPostByCurrentDate - Получить все отсортированные посты за текущий день.
     * @param pageIndex - номер открываемой страницы.
     * @param pageSize - количество постов на отображаемых страницах.
     * @param sort - параметр сортировки.
     * @return Page<Post> - отсортированная страница постов.
     * @see Post;
     */
    private Page<Post> getAllOrderedPostByCurrentDate(int pageIndex, int pageSize, Sort.Direction sort){
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize, sort, "date");

        return postRepository.findByModerationStatusValueAndIsActiveAndDateLessThanEqual(
                ModerationStatus.ACCEPTED.toString(),
                true,
                LocalDateTime.now(),
                pageable);
    }

    /**
     * Метод tryToOpenPost - Попытка открыть указанный пост.
     * Открыть пост могут:
     *  - автор поста(всегда)
     *  - модератор в случае если пост активен (параметр is_active в базе данных равен 1)
     *  - любой другой пользователей при условии, что пост:
     *     - активен (параметр is_active в базе данных равен 1),
     *     - принят модератором (параметр moderation_status равен ACCEPTED)
     *     - и время его публикации (поле timestamp) равно текущему времени или меньше его формата UTC.
     * @param post - пост который пытаются открыть.
     * @return - возврат true, если возможно открыть иначе false .
     * @see Post;
     */
    private boolean tryToOpenPost(Post post) {
        /*boolean isAuthUser = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            User reader = (User) authentication.getPrincipal();
            isAuthUser = true;

            //Автор может просматривать пост всегда
            if(reader.equals(post.getAuthor())) {
                return true;
            } else if(reader.isModerator()) {
                //Модертор может просматривать только активные посты (не черновики)
                return post.isActive();
            }
        }*/
        boolean result = false;

        try {
            //Получаем текущего пользователя
            User reader = userService.getCurrentUser();

            //Автор может просматривать пост всегда
            if(reader.equals(post.getAuthor())) {
                return true;
            } else if(reader.isModerator()) {
                //Модертор может просматривать только активные посты (не черновики)
                return post.isActive();
            }
        } catch (AuthenticationException access) {
            //не авторизованноу пользователю разрешено открывать пост, но его просмотр не будет посчитан
        } finally {
            if(post.getDate().isBefore(LocalDateTime.now())
                    && post.isActive()
                    && post.getModerationStatus() == ModerationStatus.ACCEPTED) {

                result = true;
            }
        }

        return result;
    }
}
