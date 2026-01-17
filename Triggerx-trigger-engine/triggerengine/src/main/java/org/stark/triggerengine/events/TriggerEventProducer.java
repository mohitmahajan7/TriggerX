package org.stark.triggerengine.events;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.stark.triggerengine.dtos.MarketTick;
import org.stark.triggerengine.enums.Conditions;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TriggerEventProducer {

    private final KafkaTemplate<String, TriggerFiredEvent> kafka;

    public void send(
            String triggerId,
            MarketTick tick,
            double triggerPrice,
            Conditions condition
    ) {

        TriggerFiredEvent event = new TriggerFiredEvent(
                UUID.fromString(triggerId),
                null,                    // userId not needed at engine level
                tick.symbol(),
                triggerPrice,
                tick.price(),
                condition.name(),
                Instant.now()
        );

        kafka.send("trigger-fired", triggerId, event);
    }
}
