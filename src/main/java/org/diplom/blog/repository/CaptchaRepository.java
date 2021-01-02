package org.diplom.blog.repository;

import org.diplom.blog.model.CaptchaCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Andrey.Kazakov
 * @date 25.10.2020
 */
@Repository
public interface CaptchaRepository extends CrudRepository<CaptchaCode, Long> {
    void deleteByTimeLessThan(LocalDateTime localDateTime);

    Optional<CaptchaCode> findBySecretCode(String secretCode);
}