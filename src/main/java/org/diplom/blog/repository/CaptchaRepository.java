package org.diplom.blog.repository;

import org.diplom.blog.model.CaptchaCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Andrey.Kazakov
 * @date 25.10.2020
 */
@Repository
public interface CaptchaRepository extends CrudRepository<CaptchaCode, Long> {
    /*@Query(value = "INSERT INTO captcha_codes (post_id, user_id, value) " +
            "VALUES (:postId, :userId, :value)",
            nativeQuery = true)
    CaptchaCode save(@Param("postId")long postId,
                     @Param("userId")long userId,
                     @Param("value")int value);*/

    void deleteByTimeLessThan(LocalDateTime localDateTime);

    Optional<CaptchaCode> findBySecretCode(String secretCode);
}