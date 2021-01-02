package org.diplom.blog.service;

import org.diplom.blog.api.request.CommentRequest;
import org.diplom.blog.api.response.CommentResponse;
import org.diplom.blog.dto.EntityCount;
import org.diplom.blog.dto.TagDto;
import org.diplom.blog.dto.mapper.TagMapper;
import org.diplom.blog.api.response.TagResponse;
import org.diplom.blog.model.ModerationStatus;
import org.diplom.blog.model.Tag;
import org.diplom.blog.repository.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Andrey.Kazakov
 * @date 15.08.2020
 */
@Service
public class TagService {

    private final TagsRepository tagsRepository;

    @Autowired
    public TagService(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    /**
     * Метод saveTagByListName - сохранение списка тегов.
     * Сохранение списка имен тегов (если в списке есть уже созданные теги, то они повторно создаваться не будут)
     *
     * @param listOfTagName - список наименований тегов.
     * @return List<Tag>
     * @see Tag .
     */
    @Transactional
    public List<Tag> saveTagByListName(List<String> listOfTagName){
        List<Tag> savedTags = new ArrayList<>();

        if(listOfTagName != null && listOfTagName.size() > 0){
            List<Tag> tagsFromDb = tagsRepository.findByNameIn(listOfTagName);
            List<Tag> tagsToDb = listOfTagName.parallelStream()
                    .filter(n -> tagsFromDb.parallelStream()
                            .map(Tag::getName)
                            .noneMatch(tn -> tn.equals(n)))
                    .map(Tag::new).collect(Collectors.toList());

            tagsRepository.saveAll(tagsToDb).iterator().forEachRemaining(tagsFromDb::add);
            savedTags.addAll(tagsFromDb);
        }

        return savedTags;
    }

    /**
     * Метод getTagsBySearch - получение всех тегов по шаблону запроса.
     * Данный метод выводит список тегов с их весом (количеством упоминаний в постах)
     *
     * @param search шаблон запроса.
     * @return ResponseEntity<TagResponse>
     * @see TagResponse;
     */
    public ResponseEntity<TagResponse> getTagsBySearch(String search) {
        TagResponse response = new TagResponse();

        try {
            List<EntityCount<Tag>> listTag = tagsRepository.findAllTagWithCount(search, ModerationStatus.ACCEPTED.toString(),
                    true, LocalDateTime.now());

            if (listTag != null) {
                EntityCount<Tag> tagCount = listTag.parallelStream()
                        .findFirst()
                        .orElse(new EntityCount<>(null, 0L));
                Long maxCount = tagCount.getCountRecord();

                List<TagDto> tagDtoList = listTag.parallelStream()
                        .map(tc -> TagMapper.toTagDto(tc, maxCount))
                        .collect(Collectors.toList());
                response.setTags(tagDtoList);
            }

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
