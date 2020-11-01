package org.diplom.blog.service;

import com.github.cage.GCage;
import io.jsonwebtoken.lang.Assert;
import org.diplom.blog.api.request.PostRequest;
import org.diplom.blog.model.User;
import org.diplom.blog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

/**
 * @author Andrey.Kazakov
 * @date 13.09.2020
 */
@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void addPostTest() throws Exception {
        String[] tags = "ta111, tag2, tag3".split(",\\s+");

        User user = userRepository.findById(1L).orElseThrow();
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Mapping with JPA");
        postRequest.setText("JPA entities are plain POJOs. Actually, they are Hibernate persistent entities. Their mappings are defined through JDK 5.0 annotations instead of hbm.xml files. A JPA 2 XML descriptor syntax for overriding is defined as well). Annotations can be split in two categories, the logical mapping annotations (describing the object model, the association between two entities etc.) and the physical mapping annotations (describing the physical schema, tables, columns, indexes, etc). We will mix annotations from both categories in the following code examples.\n" +
                "\n" +
                "JPA annotations are in the javax.persistence.* package. You favorite IDE can auto-complete annotations and their attributes for you (even without a specific \"JPA\" module, since JPA annotations are plain JDK 5 annotations).\n" +
                "\n" +
                "A good an complete set of working examples can be found in the Hibernate Annotations test suite itself: most of the unit tests have been designed to represent a concrete example and be a source of inspiration for you. You can get the test suite sources in the distribution.");
        postRequest.setTimestamp(new Date().getTime());
        postRequest.setActive(true);
        postRequest.setTags(tags);

        postService.addPost(postRequest);
    }
}
