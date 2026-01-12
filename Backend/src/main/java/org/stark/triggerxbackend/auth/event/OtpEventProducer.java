package org.stark.triggerxbackend.auth.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OtpEventProducer {

    private static final String TOPIC = "email-otp-events";

    private final KafkaTemplate<String,String> kafkaTemplate;

    public OtpEventProducer(KafkaTemplate<String,String> kafkaTemplate){
    this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String message){
        kafkaTemplate.send(TOPIC,message);
    }

}
