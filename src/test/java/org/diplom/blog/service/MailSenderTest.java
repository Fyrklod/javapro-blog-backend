package org.diplom.blog.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author Andrey.Kazakov
 * @date 28.10.2020
 */
@SpringBootTest
public class MailSenderTest {

    @Value("${blog.url}")
    private String blogUrl;

    @Autowired
    private MailSenderService mailSender;

    @Test
    public void mailServiceCreatedTest() {
        Assertions.assertNotNull(mailSender);
    }

    @Test
    public void mailSendTest()  {
        Exception verifiableError = null;
        try {
            String urlForRestore = String.format("%s/login/change-password/%s",
                    blogUrl, UUID.randomUUID());

            String letterText = String.format("Добрый день, %s\n" +
                    "\n" +
                    "Вы запросили восстановление пароля на нашем сайте. Для продолжения пройдите по адресу:\n" +
                    "<a href=\"%s\">%s</a>\n" +
                    "С уважением,\n" +
                    "Команда \"%s\"", "user.getFullName()", urlForRestore, urlForRestore, "siteTitle");


            mailSender.send("fyrklod@gmail.com", "testMail", letterText);
        } catch (Exception ex){
            ex.printStackTrace();
            verifiableError = ex;
        }

        Assertions.assertNull(verifiableError);
    }

}
