package org.stark.triggerengine.events;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.stark.triggerengine.dtos.MarketTick;
import org.stark.triggerengine.service.TriggerService;

@Component
@RequiredArgsConstructor
public class MarketTickListener {

    private final TriggerService triggerService;

    @KafkaListener(topics = "market-ticks")
    public void onTick(MarketTick tick) {
        triggerService.processTick(tick);
    }
}
