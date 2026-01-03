package org.stark.triggerxbackend.core.domain.model.trigger;

import java.math.BigDecimal;

public class TriggerCondition {
    private final TriggerType type;
    private final BigDecimal targetPrice;


    public TriggerCondition(TriggerType type, BigDecimal targetPrice) {
        this.type = type;
        this.targetPrice = targetPrice;
    }

    public boolean isSatisfiedBy(BigDecimal currentPrice) throws Exception{
        try{
        return switch (type) {
            case PRICE_ABOVE -> currentPrice.compareTo(targetPrice) >= 0;
            case PRICE_BELOW -> currentPrice.compareTo(targetPrice) <= 0;
        };
    } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
