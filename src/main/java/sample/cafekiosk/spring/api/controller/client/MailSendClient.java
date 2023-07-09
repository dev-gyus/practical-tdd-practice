package sample.cafekiosk.spring.api.controller.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailSendClient {
    public boolean sendMail(String fromEmail, String toEmail, String subject, String content) {
        // mail 전송
        log.info("메일 전송");
        throw new IllegalStateException("메일 전송");
    }
}
