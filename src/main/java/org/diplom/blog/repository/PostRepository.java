package org.diplom.blog.repository;

import org.diplom.blog.model.Post;
import org.diplom.blog.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Long countByModerationStatusValueAndIsActive(String moderationStatus, boolean isActive);

    Optional<Post> findByIdAndModerationStatusValueAndIsActiveAndDateLessThanEqual(Long id,
                                                                                   String moderationStatusValue,
                                                                                   boolean isActive,
                                                                                   LocalDateTime date);

    Page<Post> findByModerationStatusValueAndIsActiveAndDateLessThanEqual(String moderationStatusValue, boolean isActive,
                                                                          LocalDateTime date, Pageable pageable);

    Page<Post> findByModeratorAndIsActive(User moderator, boolean isActive, Pageable pageable);
    Page<Post> findByModeratorAndModerationStatusValueAndIsActive(User moderator, String moderationStatus
                                                                    , boolean isActive, Pageable pageable);
    Page<Post> findByModerationStatusValueAndIsActive(String moderationStatus, boolean isActive,
                                                      Pageable pageable);

    Page<Post> findByAuthorAndIsActive(User author, boolean isActive, Pageable pageable);
    Page<Post> findByAuthorAndModerationStatusValueAndIsActive(User author, String moderationStatus, boolean isActive,
                                                               Pageable pageable);

    @Query(value = "select p from Post p " +
            " where p.moderationStatusValue=:moderationStatus " +
            "       and p.isActive=:isActive " +
            "       and p.date<=:date" +
            "       and (p.title LIKE %:pattern% or p.text LIKE %:pattern%)" )
    Page<Post> searchAllByPattern(@Param("pattern") String pattern,
                                  @Param("moderationStatus") String moderationStatusValue,
                                  @Param("isActive") boolean isActive,
                                  @Param("date") LocalDateTime date,
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
                                                                 @Param("date") LocalDateTime date,
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
                                                       @Param("date") LocalDateTime date,
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
                                @Param("date") LocalDateTime date,
                                Pageable pageable);

    @Query(value = "select p.*" +
            " from posts p " +
            " where p.moderation_status=:moderationStatus " +
            "       and p.is_active=:isActive " +
            "       and date(p.time)=:date" +
            " order by time desc", nativeQuery = true)
    Page<Post> findPublishPostByDate(@Param("moderationStatus") String moderationStatusValue,
                                     @Param("isActive") boolean isActive,
                                     @Param("date") LocalDate date,
                                     Pageable pageable);

    @Query(value = "SELECT distinct EXTRACT(YEAR FROM time) as YYYY" +
                   " FROM posts " +
                   " WHERE moderation_status=:moderationStatus " +
                   "       and is_active=:isActive ", nativeQuery = true)
    List<Integer> getDistinctYearAllPosts(@Param("moderationStatus") String moderationStatusValue,
                                          @Param("isActive") boolean isActive);

    @Query(value = "SELECT text(date(time)) as dayPublication" +
                    ",   count(1) as postsCount" +
                    " FROM posts" +
                    " WHERE EXTRACT(YEAR FROM time)=:year " +
                    "       and moderation_status=:moderationStatus " +
                    "       and is_active=:isActive " +
                    " GROUP BY dayPublication" +
                    " ORDER BY dayPublication", nativeQuery = true)
    List<Object[]> getCountPostInDayOfYear(@Param("year") Integer year,
                                           @Param("moderationStatus") String moderationStatusValue,
                                           @Param("isActive") boolean isActive);

    @Query(value =  "SELECT count(id) as postsCount, " +
            "           sum(view_count) as viewsCount, " +
            "           min(time) as firstPublication, " +
            "           (SELECT count(1) FROM post_votes WHERE value > 0) as likesCount, " +
            "           (SELECT count(1) FROM post_votes WHERE value < 0) as dislikesCount " +
            "FROM posts", nativeQuery = true)
    List<Object[]> getFullStatisticOfPost();

    @Query(value =  "SELECT count(id) as postsCount, " +
            "           sum(view_count) as viewsCount, " +
            "           min(time) as firstPublication, " +
            "           (SELECT count(1) FROM post_votes WHERE value > 0 and user_id = :author) as likesCount, " +
            "           (SELECT count(1) FROM post_votes WHERE value < 0 and user_id = :author) as dislikesCount " +
            "FROM posts " +
            "WHERE user_id = :author", nativeQuery = true)
    List<Object[]> getStatisticOfPostByUser(@Param("author") long userId);
}
