package org.stark.triggerengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.stark.triggerengine.domains.MarketTrigger;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TriggerCacheService {

    private final StringRedisTemplate redis;

    public void cacheTrigger(MarketTrigger trigger) {

        String triggerId = trigger.getId().toString();

        // Symbol → trigger mapping
        redis.opsForSet()
                .add("triggers:symbol:" + trigger.getSymbol(), triggerId);

        // Trigger data
        redis.opsForHash()
                .putAll("trigger:data:" + triggerId, Map.of(
                        "userId", trigger.getUserId(),
                        "symbol", trigger.getSymbol(),
                        "condition", trigger.getCondition().name(),
                        "triggerPrice", String.valueOf(trigger.getTriggerPrice()),
                        "oneShot", String.valueOf(trigger.isOneShot()),
                        "cooldownSeconds", String.valueOf(trigger.getCooldownSeconds())
                ));
    }

    public void evictTrigger(MarketTrigger trigger) {
        String triggerId = trigger.getId().toString();

        redis.opsForSet()
                .remove("triggers:symbol:" + trigger.getSymbol(), triggerId);

        redis.delete("trigger:data:" + triggerId);
        redis.delete("trigger:cooldown:" + triggerId);
    }
}
