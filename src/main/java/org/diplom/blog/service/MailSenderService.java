package org.diplom.blog.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * @author Andrey.Kazakov
 * @date 27.10.2020
 */
@Service
public class MailSenderService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    @SneakyThrows
    public void send(String emailTo, String subject, String message) {
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, false);
        helper.setFrom(username);
        helper.setTo(emailTo);
        helper.setSubject(subject);
        mailMessage.setContent(message, "text/html");

        javaMailSender.send(mailMessage);
    }
}
