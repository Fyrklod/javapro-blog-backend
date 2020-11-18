package org.diplom.blog.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
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
    @Value("${file-storage.relative-path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path fullUploadPath = Paths.get(uploadPath).toAbsolutePath();
        /*registry.addResourceHandler("post/update/**")
                .addResourceLocations("file:/" + fullUploadPath + "/");*/
        registry.addResourceHandler("**/update/**")
                .addResourceLocations("file:/" + fullUploadPath + "/");

    }
}
