package org.diplom.blog.dto.response;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Andrey.Kazakov
 * @date 08.08.2020
 */
@Data
@Component
public class InitResponse {
    @Value("${blog.info.title}")
    private String title;

    @Value("${blog.info.subtitle}")
    private String subtitle;

    @Value("${blog.info.contact.phone}")
    private String phone;

    @Value("${blog.info.contact.email}")
    private String email;

    @Value("${blog.info.copyright}")
    private String copyright;

    @Value("${blog.info.copyrightFrom}")
    private String copyrightFrom;
}
