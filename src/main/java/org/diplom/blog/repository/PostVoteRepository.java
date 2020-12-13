package org.diplom.blog.repository;

import org.diplom.blog.model.PostVote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Andrey.Kazakov
 * @date 13.08.2020
 */
@Repository
public interface PostVoteRepository extends CrudRepository<PostVote, Long> {
    Optional<PostVote> findByPostIdAndUserId(long postId, long userId);

    @Query(value = "INSERT INTO post_votes (post_id, user_id, value) " +
                   "VALUES (:postId, :userId, :value)",
            nativeQuery = true)
    void addPostVote(@Param("postId")long postId,
                         @Param("userId")long userId,
                         @Param("value")int value);

}
