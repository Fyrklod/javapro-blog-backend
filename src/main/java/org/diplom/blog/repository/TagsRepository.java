package org.diplom.blog.repository;

import org.diplom.blog.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagsRepository extends CrudRepository<Tag, Long> {

}