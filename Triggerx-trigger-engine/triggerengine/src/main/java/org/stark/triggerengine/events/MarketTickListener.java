package org.stark.triggerengine.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.stark.triggerengine.dtos.MarketTick;
import org.stark.triggerengine.service.TriggerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketTickListener {

    private final TriggerService triggerService;

    @KafkaListener(topics = "market-ticks", groupId = "trigger-engine-group")
    public void onTick(MarketTick tick) {
        log.info(" Tick received: {} @ {}", tick.symbol(), tick.price());
        triggerService.processTick(tick);
    }
}
