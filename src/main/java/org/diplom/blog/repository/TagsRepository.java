package org.diplom.blog.repository;

import org.diplom.blog.dto.EntityCount;
import org.diplom.blog.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TagsRepository extends CrudRepository<Tag, Long> {

    @Query(value = "SELECT new org.diplom.blog.dto.EntityCount(t, COUNT(1) as cnt)" +
            " FROM Post p" +
            "  JOIN p.tags t" +
            " WHERE p.moderationStatusValue=:moderationStatus " +
            "       and p.isActive=:isActive " +
            "       and p.date<=:date" +
            "       and ((:search <> '' and t.name like %:search%) or :search = '')" +
            " GROUP BY t" +
            " ORDER BY cnt desc")
    List<EntityCount<Tag>> findAllTagWithCount(@Param("search") String search,
                                               @Param("moderationStatus") String moderationStatusValue,
                                               @Param("isActive") boolean isActive,
                                               @Param("date") LocalDateTime date);

    List<Tag> findByNameIn(List<String> names);

}