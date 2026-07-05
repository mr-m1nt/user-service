package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.event.UserEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private static final String TOPIC = "user-events";

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void sendUserCreatedEvent(String email, Long userId) {
        UserEvent event = UserEvent.builder()
                .eventType("USER_CREATED")
                .email(email)
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .build();

        sendMessage(event);
    }

    public void sendUserDeletedEvent(String email, Long userId) {
        UserEvent event = UserEvent.builder()
                .eventType("USER_DELETED")
                .email(email)
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .build();

        sendMessage(event);
    }

    private void sendMessage(UserEvent event) {
        log.info("Отправка события в Kafka: {}", event);
        kafkaTemplate.send(TOPIC, event.getEmail(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Событие отправлено: {}", event.getEventType());
                    } else {
                        log.error("❌ Ошибка отправки: {}", ex.getMessage());
                    }
                });
    }
}