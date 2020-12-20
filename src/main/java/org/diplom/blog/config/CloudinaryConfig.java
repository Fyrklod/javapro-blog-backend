package org.diplom.blog.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Andrey.Kazakov
 * @date 18.12.2020
 */
@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.apikey}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary createCloudinaryClient() {
        return new Cloudinary(
                ObjectUtils.asMap(
                         "cloud_name", cloudName,
                                "api_key", apiKey,
                                "api_secret", apiSecret
                )
        );
    }
}
