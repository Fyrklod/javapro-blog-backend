package org.diplom.blog.repository;

import org.diplom.blog.model.ModerationStatus;
import org.diplom.blog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
    Page<Post> findByModerationStatus(ModerationStatus moderationStatus, Pageable pageable);
}
