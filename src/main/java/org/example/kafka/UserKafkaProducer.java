package org.example.kafka;

import org.example.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserKafkaProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private static final String TOPIC_NAME = "user-events";

    public void sendEvent(UserEvent event) {
        log.info("Отправка события в Kafka: {}", event);
        kafkaTemplate.send(TOPIC_NAME, event.email(), event);
    }
}