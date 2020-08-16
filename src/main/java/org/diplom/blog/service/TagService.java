package org.diplom.blog.service;

import lombok.AllArgsConstructor;
import org.diplom.blog.dto.EntityCount;
import org.diplom.blog.model.ModerationStatus;
import org.diplom.blog.model.Tag;
import org.diplom.blog.repository.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Andrey.Kazakov
 * @date 15.08.2020
 */
@Service
@AllArgsConstructor
public class TagService {

    //@Autowired
    private TagsRepository tagsRepository;

    public List<EntityCount<Tag>> getTagsBySearch(String search) {
        boolean isActive = true;
        String moderationStatus = ModerationStatus.ACCEPTED.toString();
        Date currentDate = new Date();

        return tagsRepository.findAllTagWithCount(search, moderationStatus, isActive, currentDate);
    }
}
