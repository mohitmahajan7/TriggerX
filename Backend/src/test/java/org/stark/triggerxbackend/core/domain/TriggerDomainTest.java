package org.stark.triggerxbackend.core.domain;

import org.junit.jupiter.api.Test;
import org.stark.triggerxbackend.core.domain.model.trigger.*;
import org.stark.triggerxbackend.core.domain.service.TriggerEvaluationService;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;


class TriggerDomainTest {

    @Test
    void trigger_should_fire_when_price_crosses_threshold() throws Exception {
        Trigger trigger = new Trigger(
                "AAPL",
                new TriggerCondition(TriggerType.PRICE_ABOVE, new BigDecimal("150"))
        );
        trigger.activate();

        TriggerEvaluationService service = new TriggerEvaluationService();
        var event = service.evaluate(trigger, new BigDecimal("151"));

        assertTrue(event.isPresent());
        assertEquals(TriggerState.TRIGGERED, trigger.getState());
    }
}
