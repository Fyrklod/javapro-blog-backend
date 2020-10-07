package org.diplom.blog.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.diplom.blog.dto.Error;
import org.diplom.blog.dto.Mode;
import org.diplom.blog.api.response.PostResponse;
import org.diplom.blog.dto.PostStatus;
import org.diplom.blog.dto.mapper.PostMapper;
import org.diplom.blog.api.request.PostRequest;
import org.diplom.blog.api.response.CommonResponse;
import org.diplom.blog.api.response.PostListResponse;
import org.diplom.blog.api.response.UploadResponse;
import org.diplom.blog.model.*;
import org.diplom.blog.repository.PostRepository;
import org.diplom.blog.repository.PostVoteRepository;
import org.diplom.blog.utils.DateUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final TagService tagService;
    private final GeneralService generalService;

    @Transactional
    public ResponseEntity<PostResponse> getPostById(Long id, User reader){

        //Метод выводит данные конкретного поста для отображения на странице поста, в том числе,
        // список комментариев и тэгов, привязанных к данному посту. Выводит пост в любом случае,
        // если пост активен (параметр is_active в базе данных равен 1),
        // принят модератором (параметр moderation_status равен ACCEPTED)
        // и время его публикации (поле timestamp) равно текущему времени или меньше его формата UTC.
        Optional<Post> optionalPost = postRepository.findByIdAndModerationStatusValueAndIsActiveAndDateLessThanEqual(id,
                ModerationStatus.ACCEPTED.toString(),
                true,
                LocalDateTime.now());

        if(optionalPost.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Post post = optionalPost.get();

        if(!reader.isModerator() && !reader.equals(post.getAuthor())) {
            post.viewPost();
            postRepository.save(post);
        }

        PostResponse response = PostMapper.toPostResponse(post);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<PostListResponse> getPosts(int offset, int limit, Mode mode) {
        Page<Post> pages = null;

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

    public ResponseEntity<PostListResponse> getPostForModeration(ModerationStatus status, User currentUser, int pageIndex, int pageSize){

        try {

            if(!currentUser.isModerator()){
                return ResponseEntity.status(HttpStatus.LOCKED).body(null);
            }

            Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);
            Page<Post> pages = status.equals(ModerationStatus.NEW)
                           ? postRepository.findByModerationStatusValueAndIsActive(status.toString(),
                                                                             true, pageable)
                           : postRepository.findByModeratorAndIsActive(currentUser, true, pageable);

            return preparePostsResponse(pages);

        } catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @SneakyThrows
    public ResponseEntity<PostListResponse> getMyPosts(PostStatus postStatus, User currentUser,
                                                       int pageIndex, int pageSize){

        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);
        Page<Post> pages = postStatus.equals(PostStatus.INACTIVE)
                ? postRepository.findByAuthorAndIsActive(currentUser, false, pageable)
                : postRepository.findByAuthorAndModerationStatusValueAndIsActive(currentUser,
                                        ModerationStatus.fromPostStatus(postStatus).toString(),
                                        true, pageable);

        return preparePostsResponse(pages);
    }

    public ResponseEntity<UploadResponse> addPost(PostRequest request, User author) {
        return savePost(0L, request, author);
    }

    public ResponseEntity<UploadResponse> editPost(Long id, PostRequest request, User author){
        return savePost(id, request, author);
    }

    public ResponseEntity<CommonResponse> savePostVote(Long postId, User user, int value){
        CommonResponse response = new CommonResponse();
        boolean result = true;
        PostVote postVote = null;

        try {
            Optional<PostVote> optionalPostVote =  postVoteRepository.findByPostIdAndUserId(postId, user.getId());
            if(optionalPostVote.isPresent())
            {
                postVote = optionalPostVote.get();
                if(postVote.getValue().equals(value)){
                    response.setResult(false);
                    return ResponseEntity.ok(response);
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

        response.setResult(result);
        return ResponseEntity.ok(response);
    }

    //TODO: статус поста при отключенной настройки ПРЕМОДЕРАЦИЯ и если это черновик ?
    @Transactional
    private ResponseEntity<UploadResponse> savePost(Long id, PostRequest request, User author) {
        UploadResponse response;
        Boolean postPremoderation = generalService.getSettingValueByCode("POST_PREMODERATION");

        if(request.getTitle().length() > 3 && request.getText().length() > 50){
            List<Tag> tags = tagService.saveTagByListName(Arrays.asList(request.getTags()));
            LocalDateTime postDateTime = DateUtil.getLocalDateTimeFromTimestamp(request.getTimestamp());

            Post post = ( id > 0 )
                            ? postRepository.getOne(id)
                            : new Post();

            post.setTitle(request.getTitle())
                    .setText(request.getText())
                    .setAuthor(author)
                    .setTags(tags)
                    .setActive(request.isActive())
                    .setDate(
                            postDateTime.compareTo(LocalDateTime.now()) > 0
                                    ? LocalDateTime.now()
                                    : postDateTime
                    )
                    .setModerationStatus(
                            //EСЛИ настройка ПРЕМОДЕРАЦИЯ включена, тогда все статусы NEW
                            //ИНАЧЕ ACCEPT (тут есть "НО", доп условие "если у них установлен параметр active = 1")
                            //??? если у них установлен параметр active = 0 то какой устанавливать статус???
                            postPremoderation
                                ? ModerationStatus.NEW
                                : ModerationStatus.ACCEPTED
                    );

            postRepository.save(post);

            response = UploadResponse.builder()
                                    .result(true).build();
        } else {
            Error error = new Error();
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

            List<PostResponse> postResponseList = posts.parallelStream()
                    .map(PostMapper::toPostResponse)
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

        return postRepository.findAllWithCountOfCommentsOrderByCountDesc(ModerationStatus.ACCEPTED.toString(),
                true,
                LocalDateTime.now(),
                pageable
        );
    }

    private Page<Post> getAllBestPost(int pageIndex, int pageSize){
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);
        return postRepository.findAllWithCountOfVotesOrderByCountDesc(ModerationStatus.ACCEPTED.toString(),
                true,
                LocalDateTime.now(),
                pageable
        );
    }

    private Page<Post> getAllPostOrderedByDate(int pageIndex, int pageSize, Sort.Direction sort){
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize, sort, "date");

        return postRepository.findByModerationStatusValueAndIsActiveAndDateLessThanEqual(ModerationStatus.ACCEPTED.toString(), true,  LocalDateTime.now(), pageable);
    }

}
