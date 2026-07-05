package org.example.notification.consumer;

import com.example.notification.dto.UserEvent;
import com.example.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEvent event) {
        log.info("📥 Получено событие: {}", event);

        try {
            switch (event.getEventType()) {
                case "USER_CREATED" -> {
                    log.info("Отправка приветственного письма на {}", event.getEmail());
                    emailService.sendWelcomeEmail(event.getEmail());
                }
                case "USER_DELETED" -> {
                    log.info("Отправка прощального письма на {}", event.getEmail());
                    emailService.sendGoodbyeEmail(event.getEmail());
                }
                default -> log.warn("Неизвестный тип события: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки события: {}", e.getMessage());
            // В реальном проекте тут был бы Dead Letter Queue
        }
    }
}