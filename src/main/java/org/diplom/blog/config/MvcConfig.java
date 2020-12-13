package org.diplom.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Andrey.Kazakov
 * @date 14.11.2020
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Value("${file-storage.relative-path.post}")
    private String uploadPostPath;
    @Value("${file-storage.relative-path.avatar}")
    private String uploadAvatarPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path fullUploadPath = Paths.get(uploadPostPath).toAbsolutePath();
        Path fullAvatarPath = Paths.get(uploadAvatarPath).toAbsolutePath();

        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:/" + fullUploadPath + "/");
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file:/" + fullAvatarPath + "/");
    }
}
