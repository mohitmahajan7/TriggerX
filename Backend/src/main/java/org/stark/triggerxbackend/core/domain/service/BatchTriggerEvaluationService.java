package org.stark.triggerxbackend.core.domain.service;

import org.stark.triggerxbackend.core.domain.event.TriggerFiredEvent;
import org.stark.triggerxbackend.core.domain.model.market.MarketPrice;
import org.stark.triggerxbackend.core.domain.model.trigger.Trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BatchTriggerEvaluationService {

    private final TriggerEvaluationService evaluationService =
            new TriggerEvaluationService();

    public List<TriggerFiredEvent> evaluate(
            List<Trigger> triggers,
            MarketPrice marketPrice
    ) throws Exception {
        List<TriggerFiredEvent> firedEvents = new ArrayList<>();

        for (Trigger trigger : triggers) {

            // symbol filtering is CRITICAL for scale
            if (!trigger.getSymbol().equals(marketPrice.getSymbol())) {
                continue;
            }

            Optional<TriggerFiredEvent> event =
                    evaluationService.evaluate(trigger, marketPrice.getPrice());

            event.ifPresent(firedEvents::add);
        }

        return firedEvents;
    }
}
