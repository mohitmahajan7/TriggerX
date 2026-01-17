package org.stark.triggerengine.events;

import java.time.Instant;
import java.util.UUID;

public record TriggerFiredEvent(
        UUID triggerId,
        String userId,
        String symbol,
        double triggerPrice,
        double marketPrice,
        String condition,
        Instant firedAt
) {}
