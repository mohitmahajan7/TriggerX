package org.stark.triggerengine.events;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.stark.triggerengine.domains.MarketTrigger;
import org.stark.triggerengine.enums.TriggerStatus;
import org.stark.triggerengine.repositories.MarketTriggerRepository;

@Component
@RequiredArgsConstructor
public class TriggerFiredConsumer {

    private final MarketTriggerRepository repo;

    @KafkaListener(
            topics = "trigger-fired",
            groupId = "trigger-db-sync"
    )
    @Transactional
    public void handle(TriggerFiredEvent event) {

        MarketTrigger trigger = repo.findById(event.triggerId())
                .orElseThrow();

        trigger.setLastTriggeredAt(event.firedAt());

        if (trigger.isOneShot()) {
            trigger.setStatus(TriggerStatus.FIRED);
        }

        repo.save(trigger);
    }
}
