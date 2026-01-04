package org.stark.triggerxbackend.core.domain.model.trigger;
import lombok.Getter;

import java.util.UUID;

@Getter
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

}
