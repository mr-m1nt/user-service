package org.example.notification.service;

import com.example.notification.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String FROM_EMAIL = "noreply@user-service.com";

    private final JavaMailSender mailSender;

    public void sendEmail(EmailRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(request.getTo());
            message.setSubject(request.getSubject());
            message.setText(request.getBody());

            mailSender.send(message);
            log.info("✅ Email отправлен на {}", request.getTo());
        } catch (Exception e) {
            log.error("❌ Ошибка отправки email на {}: {}",
                    request.getTo(), e.getMessage());
            throw new RuntimeException("Не удалось отправить email", e);
        }
    }

    public void sendWelcomeEmail(String email) {
        EmailRequest request = EmailRequest.builder()
                .to(email)
                .subject("Добро пожаловать!")
                .body("Здравствуйте! Ваш аккаунт на сайте был успешно создан.")
                .build();
        sendEmail(request);
    }

    public void sendGoodbyeEmail(String email) {
        EmailRequest request = EmailRequest.builder()
                .to(email)
                .subject("Аккаунт удалён")
                .body("Здравствуйте! Ваш аккаунт был удалён.")
                .build();
        sendEmail(request);
    }
}