package org.diplom.blog.repository;

import org.diplom.blog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);
    Page<Post> findByModerationStatusValueAndIsActiveAndDateLessThanEqual(String moderationStatusValue, boolean isActive,
                                                                          Date date, Pageable pageable);

    //Page<Post> findByModerationStatusValueAndIsActiveAndDateLessThanEqualAndTitleContainsOrTextContains(String title, String text, Pageable pageable);

    @Query(value = "select p from Post p " +
            " where p.moderationStatusValue=:moderationStatus " +
            "       and p.isActive=:isActive " +
            "       and p.date<=:date" +
            "       and (p.title LIKE %:pattern% or p.text LIKE %:pattern%)" )
    Page<Post> searchAllByPattern(@Param("pattern") String pattern,
                                  @Param("moderationStatus") String moderationStatusValue,
                                  @Param("isActive") boolean isActive,
                                  @Param("date") Date date,
                                  Pageable pageable);

    @Query(value = "select p" +
                    " from Post p left join p.postComments ref " +
                    " where p.moderationStatusValue=:moderationStatus " +
                    "       and p.isActive=:isActive " +
                    "       and p.date<=:date" +
                    " group by p " +
                    " order by count(ref.id) desc" )
    Page<Post> findAllWithCountOfCommentsOrderByCountDesc(@Param("moderationStatus") String moderationStatusValue,
                                                                 @Param("isActive") boolean isActive,
                                                                 @Param("date") Date date,
                                                                 Pageable pageable);

    @Query(value = "select p.*, sum(COALESCE(pv.value, 0)) as rating" +
            " from posts p left join post_votes pv on pv.post_id=p.id" +
            " where p.moderation_status=:moderationStatus " +
            "       and p.is_active=:isActive " +
            "       and p.time<=:date" +
            " group by p.id, p.is_active, p.moderation_status, p.moderator_id, p.user_id, p.time, p.title, p.text, p.view_count " +
            " order by rating desc, time desc", nativeQuery = true)
    Page<Post> findAllWithCountOfVotesOrderByCountDesc(@Param("moderationStatus") String moderationStatusValue,
                                                       @Param("isActive") boolean isActive,
                                                       @Param("date") Date date,
                                                       Pageable pageable);

    @Query(value = "select p" +
            " from Post p join p.tags t " +
            " where t.name=:tagName" +
            "       and p.moderationStatusValue=:moderationStatus " +
            "       and p.isActive=:isActive " +
            "       and p.date<=:date" +
            " order by p.date desc" )
    Page<Post> findAllByTagName(@Param("tagName") String tagName,
                                @Param("moderationStatus") String moderationStatusValue,
                                @Param("isActive") boolean isActive,
                                @Param("date") Date date,
                                Pageable pageable);
}
