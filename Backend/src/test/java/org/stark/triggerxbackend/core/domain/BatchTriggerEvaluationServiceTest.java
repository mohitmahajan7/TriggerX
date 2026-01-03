package org.stark.triggerxbackend.core.domain;

import org.junit.jupiter.api.Test;
import org.stark.triggerxbackend.core.domain.model.market.MarketPrice;
import org.stark.triggerxbackend.core.domain.model.trigger.*;
import org.stark.triggerxbackend.core.domain.service.BatchTriggerEvaluationService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BatchTriggerEvaluationServiceTest {

    @Test
    void should_fire_only_matching_symbol_triggers() throws Exception {
        Trigger t1 = new Trigger(
                "AAPL",
                new TriggerCondition(TriggerType.PRICE_ABOVE, new BigDecimal("150"))
        );
        t1.activate();

        Trigger t2 = new Trigger(
                "GOOG",
                new TriggerCondition(TriggerType.PRICE_ABOVE, new BigDecimal("100"))
        );
        t2.activate();

        MarketPrice price = new MarketPrice(
                "AAPL",
                new BigDecimal("155"),
                Instant.now()
        );

        BatchTriggerEvaluationService service =
                new BatchTriggerEvaluationService();

        var events = service.evaluate(List.of(t1, t2), price);

        assertEquals(1, events.size());
        assertEquals(TriggerState.TRIGGERED, t1.getState());
        assertEquals(TriggerState.ACTIVE, t2.getState());
    }
}
