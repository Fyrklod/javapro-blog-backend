package org.diplom.blog.service;

import lombok.AllArgsConstructor;
import org.diplom.blog.model.ModerationStatus;
import org.diplom.blog.model.Post;
import org.diplom.blog.model.PostVote;
import org.diplom.blog.repository.PostRepository;
import org.diplom.blog.repository.PostVoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {
    //@Autowired
    private PostRepository postRepository;
    //@Autowired
    private PostVoteRepository postVoteRepository;

    public Post getPostById(Long id){
        return postRepository.getOne(id);
    }

    private Page<Post> getAllPostOrderedByDate(int pageIndex, int pageSize, Sort.Direction sort){
        boolean isActive = true;
        String moderationStatus = ModerationStatus.ACCEPTED.toString();
        Date currentDate = new Date();
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize, sort, "date");

        return postRepository.findByModerationStatusValueAndIsActiveAndDateLessThanEqual(moderationStatus, isActive, currentDate, pageable);
    }

    public Page<Post> getAllEarlyPost(int pageIndex, int pageSize){
        /*boolean isActive = true;
        String moderationStatus = ModerationStatus.ACCEPTED.toString();
        Date currentDate = new Date();
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize, Sort.Direction.ASC, "date");

        return postRepository.findByModerationStatusValueAndIsActiveAndDateLessThanEqual(moderationStatus, isActive, currentDate, pageable);*/
        return getAllPostOrderedByDate(pageIndex, pageSize, Sort.Direction.ASC);
    }

    public Page<Post> getAllRecentPost(int pageIndex, int pageSize){
        /*boolean isActive = true;
        String moderationStatus = ModerationStatus.ACCEPTED.toString();
        Date currentDate = new Date();
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize, Sort.Direction.DESC, "date");

        return postRepository.findByModerationStatusValueAndIsActiveAndDateLessThanEqual(moderationStatus, isActive, currentDate, pageable);*/
        return getAllPostOrderedByDate(pageIndex, pageSize, Sort.Direction.DESC);
    }

    public Page<Post> getAllPopularPost(int pageIndex, int pageSize){
        boolean isActive = true;
        String moderationStatus = ModerationStatus.ACCEPTED.toString();
        Date currentDate = new Date();
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);

        return postRepository.findAllWithCountOfCommentsOrderByCountDesc(moderationStatus, isActive,
                                                                        currentDate, pageable);
    }

    public Page<Post> getAllBestPost(int pageIndex, int pageSize){
        boolean isActive = true;
        String moderationStatus = ModerationStatus.ACCEPTED.toString();
        Date currentDate = new Date();
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);
        return postRepository.findAllWithCountOfVotesOrderByCountDesc(moderationStatus, isActive,
                                                                        currentDate, pageable);
    }

    public Page<Post> searchPosts(String pattern, int pageIndex, int pageSize) {
        boolean isActive = true;
        String moderationStatus = ModerationStatus.ACCEPTED.toString();
        Date currentDate = new Date();
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);
        return postRepository.searchAllByPattern(pattern,moderationStatus,isActive, currentDate, pageable);
    }

    public Page<Post> getAllByTag(String tag, int pageIndex, int pageSize){
        boolean isActive = true;
        String moderationStatus = ModerationStatus.ACCEPTED.toString();
        Date currentDate = new Date();
        Pageable pageable = PageRequest.of(pageIndex/pageSize, pageSize);

        return postRepository.findAllByTagName(tag, moderationStatus, isActive, currentDate, pageable);
    }

    public boolean dislike(long postId, long userId){
        return savePostVote(postId, userId, -1);
    }

    public boolean like(long postId, long userId){
        return savePostVote(postId, userId, 1);
    }

    private boolean savePostVote(long postId, long userId, int value){
        boolean result = true;
        PostVote postVote = null;
        try {
            Optional<PostVote> optionalPostVote =  postVoteRepository.findByPostIdAndUserId(postId, userId);
            if(optionalPostVote.isPresent())
            {
                postVote = optionalPostVote.get();
                if(postVote.getValue().equals(value)){
                    return false;
                } else {
                    postVote.setValue(value);
                }
            } else {
                postVote = new PostVote(postId, userId, value);
            }
            postVoteRepository.save(postVote);

        } catch (Exception ex){
            ex.printStackTrace();
            result = false;
        }

        return result;
    }
}
