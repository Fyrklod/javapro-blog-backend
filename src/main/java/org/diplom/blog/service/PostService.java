package org.diplom.blog.service;

import lombok.SneakyThrows;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Transactional
    public ResponseEntity<PostResponse> getPostById(Long id){
        //Метод выводит данные конкретного поста для отображения на странице поста, в том числе,
        // список комментариев и тэгов, привязанных к данному посту. Выводит пост в любом случае,
        // если пост активен (параметр is_active в базе данных равен 1),
        // принят модератором (параметр moderation_status равен ACCEPTED)
        // и время его публикации (поле timestamp) равно текущему времени или меньше его формата UTC.
        Optional<Post> optionalPost = postRepository.findById(id);

        if(optionalPost.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Post post = optionalPost.get();
        if(!tryToSeePost(post)) {
            return ResponseEntity.notFound().build();
        }

        PostResponse response = PostMapper.toSinglePostResponse(post);
        return ResponseEntity.ok(response);
    }

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

    public ResponseEntity<PostListResponse> getPostForModeration(ModerationStatus status, int pageIndex, int pageSize){
        try {
            User currentUser = userService.getCurrentUser();

            if(!currentUser.isModerator()){
                return ResponseEntity.status(HttpStatus.LOCKED).body(null);
            }

            Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);
            Page<Post> pages = status.equals(ModerationStatus.NEW)
                           ? postRepository.findByModerationStatusValueAndIsActive(status.toString(),
                                                                             true, pageable)
                           : postRepository.findByModeratorAndModerationStatusValueAndIsActive(currentUser
                                                                            , status.toString(), true, pageable);
            return preparePostsResponse(pages);

        } catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

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

    public ResponseEntity<UploadResponse> addPost(PostRequest request)throws Exception {
        return savePost(0L, request);
    }

    public ResponseEntity<UploadResponse> editPost(Long id, PostRequest request) throws Exception{
        return savePost(id, request);
    }

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

    @Transactional
    private ResponseEntity<UploadResponse> savePost(Long id, PostRequest request) throws Exception {
        UploadResponse response;
        Boolean postPremoderation = settingService.getBooleanSettingValueByCode("POST_PREMODERATION");

        if(request.getTitle().length() > 3 && request.getText().length() > 50){
            List<Tag> tags = tagService.saveTagByListName(Arrays.asList(request.getTags()));
            LocalDateTime postDateTime = DateUtil.getLocalDateTimeFromTimestamp(request.getTimestamp());

            Post post = ( id > 0 ) ? postRepository.getOne(id)
                                   : new Post();
            User currentUser = userService.getCurrentUser();
/*            User author = ( id > 0 ) ? post.getAuthor()
                                     : currentUser;*/

            if(id == 0 ) {
                post.setViewCount(0);
                post.setAuthor(currentUser);
            }

            post.setTitle(request.getTitle())
                .setText(request.getText())
                .setTags(tags)
                .setActive(request.isActive())
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
            if(request.getTitle().length() <= 3){
                error.setTitle("Заголовок не установлен");
            }

            if(request.getText().length() <= 50){
                error.setText("Текст публикации слишком короткий");
            }

            response = UploadResponse.builder()
                                    .result(false)
                                    .errors(error).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

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

    private Page<Post> getAllEarlyPost(int pageIndex, int pageSize){
        return getAllPostOrderedByDate(pageIndex, pageSize, Sort.Direction.ASC);
    }

    private Page<Post> getAllRecentPost(int pageIndex, int pageSize){
        return getAllPostOrderedByDate(pageIndex, pageSize, Sort.Direction.DESC);
    }

    private Page<Post> getAllPopularPost(int pageIndex, int pageSize){
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);

        return postRepository.findAllWithCountOfCommentsOrderByCountDesc(
                ModerationStatus.ACCEPTED.toString(),
                true,
                LocalDateTime.now(),
                pageable
        );
    }

    private Page<Post> getAllBestPost(int pageIndex, int pageSize){
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);
        return postRepository.findAllWithCountOfVotesOrderByCountDesc(
                ModerationStatus.ACCEPTED.toString(),
                true,
                LocalDateTime.now(),
                pageable
        );
    }

    private Page<Post> getAllPostOrderedByDate(int pageIndex, int pageSize, Sort.Direction sort){
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize, sort, "date");

        return postRepository.findByModerationStatusValueAndIsActiveAndDateLessThanEqual(
                ModerationStatus.ACCEPTED.toString(),
                true,
                LocalDateTime.now(),
                pageable);
    }

    private boolean tryToSeePost(Post post) {
        boolean isAuthUser = false;
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
        }

        //если пользователь не привелигированный (Автор или Модератор),
        // то смотреть может только посты:
        // - у который дата публикации меньше текущей
        // - активный пост (не черновик)
        // - принятый можератором
        if(post.getDate().isBefore(LocalDateTime.now())
                && post.isActive()
                && post.getModerationStatus() == ModerationStatus.ACCEPTED) {

            if(isAuthUser) {
                post.viewPost();
                postRepository.save(post);
            }

            return true;
        }

        return false;
    }
}
