package org.diplom.blog.repository;

import org.diplom.blog.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<PostComment, Long> {
}
