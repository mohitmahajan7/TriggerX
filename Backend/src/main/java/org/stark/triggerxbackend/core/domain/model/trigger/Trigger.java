package org.stark.triggerxbackend.core.domain.model.trigger;
import java.math.BigDecimal;
import java.util.UUID;

public class Trigger {

    private final String id;
    private final String symbol;
    private final TriggerCondition condition;
    private TriggerState state;

    public Trigger(String symbol, TriggerCondition condition) {
        this.id = UUID.randomUUID().toString();
        this.symbol = symbol;
        this.condition = condition;
        this.state = TriggerState.CREATED;
    }

    public void activate() {
        if (state != TriggerState.CREATED) {
            throw new IllegalStateException(
                    "Trigger cannot be activated from state: " + state
            );
        }
        this.state = TriggerState.ACTIVE;
    }

    public boolean canEvaluate() {
        return state == TriggerState.ACTIVE;
    }

    public void markTriggered() {
        this.state = TriggerState.TRIGGERED;
    }

    public TriggerState getState() {
        return state;
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public TriggerCondition getCondition() {
        return condition;
    }
}
