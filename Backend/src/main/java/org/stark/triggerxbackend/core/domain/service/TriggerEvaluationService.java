package org.stark.triggerxbackend.core.domain.service;

import org.stark.triggerxbackend.core.domain.event.TriggerFiredEvent;
import org.stark.triggerxbackend.core.domain.model.trigger.Trigger;

import java.math.BigDecimal;
import java.util.Optional;

public class TriggerEvaluationService {

        public Optional<TriggerFiredEvent> evaluate(
                Trigger trigger,
                BigDecimal marketPrice
        ) throws Exception {
            if (!trigger.canEvaluate()) {
                return Optional.empty();
            }

            try {
                if (trigger.getCondition().isSatisfiedBy(marketPrice)) {
                    trigger.markTriggered();
                    return Optional.of(new TriggerFiredEvent(trigger.getId()));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return Optional.empty();
        }
    }
