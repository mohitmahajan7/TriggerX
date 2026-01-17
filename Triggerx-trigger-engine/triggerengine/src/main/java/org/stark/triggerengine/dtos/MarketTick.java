package org.stark.triggerengine.dtos;

import java.time.Instant;

public record MarketTick(
        String symbol,
        double price,
        Instant timestamp
) {}
