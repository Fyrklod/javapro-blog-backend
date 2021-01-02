package org.diplom.blog.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author Andrey.Kazakov
 * @date 27.10.2020
 */
@Slf4j
@Service
public class MailSenderService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    /**
     * Метод sendMail.
     * Отправка email'а.
     *
     * @param emailTo - адрес получателя.
     * @param subject - тебя письма.
     * @param message - отправляемое сообщение.
     */
    @SneakyThrows
    public void sendMail(String emailTo, String subject, String message) {
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, false, "utf-8");
            helper.setFrom(username);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            mailMessage.setContent(message, "text/html; charset=UTF-8");

            javaMailSender.send(mailMessage);
        }  catch (MessagingException ex) {
            log.error(ex.getMessage());
        }
    }
}
