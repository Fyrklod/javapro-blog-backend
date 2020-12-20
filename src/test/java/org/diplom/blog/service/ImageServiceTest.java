package org.diplom.blog.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.diplom.blog.dto.AbstractError;
import org.diplom.blog.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author Andrey.Kazakov
 * @date 18.12.2020
 */
@SpringBootTest
public class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @Test
    public void imageDeleteTest() throws Exception {
        try {
            imageService.deleteImage("photo");
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assertions.fail();
        }
    }
}
