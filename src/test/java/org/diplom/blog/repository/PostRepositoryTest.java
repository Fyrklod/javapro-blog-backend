package org.diplom.blog.repository;

import org.diplom.blog.model.ModerationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author Andrey.Kazakov
 * @date 16.09.2020
 */
@SpringBootTest
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Test
    public void getCountPostInDayOfYear() {
        List<Object[]> info = postRepository.getCountPostInDayOfYear(2020
                                                        , ModerationStatus.ACCEPTED.toString(),
                                                        true);
        Object[] obj = info.get(0);
        String year = (String)obj[0];
        long count =((BigInteger)obj[1]).longValue();

        Assertions.assertNotNull(obj);
    }
}
