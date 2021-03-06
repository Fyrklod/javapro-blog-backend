package org.diplom.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * @author Andrey.Kazakov
 * @date 27.10.2020
 */
@Configuration
public class MailConfig {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.protocol}")
    private String protocol;

    @Value("${mail.debug}")
    private String debug;

    @Value("${mail.smtp.starttls.enable}")
    private String ttlEnable;

    @Value("${mail.smtp.auth}")
    private String isAuth;

    @Bean
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setProtocol(protocol);

        Properties prop = mailSender.getJavaMailProperties();
        prop.setProperty("mail.smtp.auth", isAuth);
        prop.setProperty("mail.smtp.starttls.enable", ttlEnable);
        prop.setProperty("mail.debug", debug);

        return mailSender;
    }
}
