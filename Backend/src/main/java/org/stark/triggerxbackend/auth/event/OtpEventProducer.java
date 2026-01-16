package org.stark.triggerxbackend.auth.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OtpEventProducer {

    private static final String TOPIC = "email-otp-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OtpEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(OtpEventPayload payload) {
        kafkaTemplate.send(TOPIC, payload.email(), payload);
    }
}
