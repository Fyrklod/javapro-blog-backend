package org.diplom.blog.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Andrey.Kazakov
 * @date 28.10.2020
 */
@SpringBootTest
public class MailSenderTest {

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
            mailSender.send("fyrklod@gmail.com", "testMail", "Hello, <b>group!</b>");//test-sd1bkelkj@srv1.mail-tester.com
        } catch (Exception ex){
            ex.printStackTrace();
            verifiableError = ex;
        }

        Assertions.assertNull(verifiableError);
    }

}
