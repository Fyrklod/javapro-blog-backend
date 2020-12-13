package org.diplom.blog.service;

import lombok.SneakyThrows;
import org.diplom.blog.dto.Decision;
import org.diplom.blog.dto.ImageError;
import org.diplom.blog.dto.ImageType;
import org.diplom.blog.api.request.ModerationRequest;
import org.diplom.blog.api.response.*;
import org.diplom.blog.exception.UploadImageException;
import org.diplom.blog.model.ModerationStatus;
import org.diplom.blog.model.Post;
import org.diplom.blog.model.User;
import org.diplom.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andrey.Kazakov
 * @date 08.08.2020
 */
@Service
public class GeneralService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public GeneralService(UserService userService,
                          ImageService imageService,
                          PostRepository postRepository) {
        this.userService = userService;
        this.imageService = imageService;
        this.postRepository = postRepository;
    }

    public ResponseEntity<CalendarResponse> getCalendar(String year) {

        Pattern pattern = Pattern.compile("(\\d{4})");
        Matcher matcher = pattern.matcher(year);
        DateFormat dateFormat = new SimpleDateFormat("yyyy");

        if(year.isBlank()){
            year = dateFormat.format(new Date());
        } else if(!matcher.find()){
            return ResponseEntity.badRequest().build();
        }

        List<Integer> listOfYear = postRepository.getDistinctYearAllPosts(ModerationStatus.ACCEPTED.toString(),
                                                                            true);
        List<Object[]> postCount = postRepository.getCountPostInDayOfYear(Integer.parseInt(year),
                                                                          ModerationStatus.ACCEPTED.toString(),
                                                                          true);
        Set<Integer> years = new HashSet<>(listOfYear);
        Map<String, Long> postCountMap = new HashMap<>();
        postCount.parallelStream()
                .forEach(obj -> postCountMap.put((String) obj[0], ((BigInteger)obj[1]).longValue()));

        CalendarResponse response = new CalendarResponse(years, postCountMap);

        return ResponseEntity.ok(response);
    }

    @SneakyThrows
    public ResponseEntity<String> addImage(MultipartFile upload) {
        try {
            String uploadPath = imageService.uploadImage(upload, ImageType.POST_IMAGE);
            return ResponseEntity.ok(uploadPath);
        } catch (InvalidParameterException ex) {
            throw new UploadImageException(new ImageError(ex.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<SimpleResponse> moderationPost(ModerationRequest request) {
        boolean result;

        try {
            Post post = postRepository.findById(request.getPostId())
                            .orElseThrow(() -> new Exception("Пост не найден"));
            User moderator = userService.getCurrentUser();
            post.setModerationStatusValue(
                    request.getDecision().equals(Decision.ACCEPT)
                            ? ModerationStatus.ACCEPTED.toString()
                            : ModerationStatus.DECLINED.toString()
            );
            post.setModerator(moderator);
            postRepository.save(post);

            result = true;
        } catch (Exception ex) {
            result = false;
        }

        return ResponseEntity.ok(new SimpleResponse(result));
    }
}
