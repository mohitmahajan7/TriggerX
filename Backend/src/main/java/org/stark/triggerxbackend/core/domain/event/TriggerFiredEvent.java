package org.stark.triggerxbackend.core.domain.event;

import java.time.Instant;

public class TriggerFiredEvent {

    private final String triggerId;
    private final Instant occurredAt;

    public TriggerFiredEvent(String triggerId) {
        this.triggerId = triggerId;
        this.occurredAt = Instant.now();
    }

    public String getTriggerId() {
        return triggerId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
