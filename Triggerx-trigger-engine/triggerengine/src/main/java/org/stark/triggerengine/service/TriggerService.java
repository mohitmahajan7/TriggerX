package org.stark.triggerengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.stark.triggerengine.dtos.MarketTick;
import org.stark.triggerengine.enums.Conditions;
import org.stark.triggerengine.events.TriggerEventProducer;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TriggerService {

    private final StringRedisTemplate redis;
    private final TriggerEventProducer producer;

    public void processTick(MarketTick tick) {

        String symbolKey = "triggers:symbol:" + tick.symbol();
        Set<String> triggerIds = redis.opsForSet().members(symbolKey);

        if (triggerIds == null || triggerIds.isEmpty()) return;

        HashOperations<String, String, String> hash = redis.opsForHash();

        for (String triggerId : triggerIds) {

            String dataKey = "trigger:data:" + triggerId;
            String cooldownKey = "trigger:cooldown:" + triggerId;

            // ================= COOLDOWN CHECK =================
            if (Boolean.TRUE.equals(redis.hasKey(cooldownKey))) {
                updateLastPrice(hash, dataKey, tick.price());
                continue;
            }

            // ================= READ TRIGGER DATA =================
            String triggerPriceStr = hash.get(dataKey, "triggerPrice");
            String conditionStr = hash.get(dataKey, "condition");

            if (triggerPriceStr == null || conditionStr == null) continue;

            double triggerPrice = Double.parseDouble(triggerPriceStr);
            Conditions condition = Conditions.valueOf(conditionStr);

            boolean oneShot = Boolean.parseBoolean(Optional.ofNullable(hash.get(dataKey, "oneShot")).orElse("true"));

            int cooldownSeconds = Integer.parseInt(Optional.ofNullable(hash.get(dataKey, "cooldownSeconds")).orElse("0"));

            Double lastPrice = Optional.ofNullable(hash.get(dataKey, "lastEvaluatedPrice")).map(Double::parseDouble).orElse(null);

            // First tick — only initialize price
            if (lastPrice == null) {
                updateLastPrice(hash, dataKey, tick.price());
                continue;
            }

            // ================= CROSSING LOGIC =================
            boolean crossed = condition == Conditions.ABOVE ? lastPrice < triggerPrice && tick.price() >= triggerPrice : lastPrice > triggerPrice && tick.price() <= triggerPrice;

            if (crossed) {

                producer.send(triggerId, tick, triggerPrice, condition);

                // Cooldown lock
                if (cooldownSeconds > 0) {
                    redis.opsForValue().set(cooldownKey, "1", cooldownSeconds, TimeUnit.SECONDS);
                }

                // Remove one-shot trigger
                if (oneShot) {
                    redis.opsForSet().remove(symbolKey, triggerId);
                }
            }

            updateLastPrice(hash, dataKey, tick.price());
        }
    }

    // ================= HELPERS =================

    private void updateLastPrice(HashOperations<String, String, String> hash, String key, double price) {
        hash.put(key, "lastEvaluatedPrice", String.valueOf(price));
        hash.put(key, "lastEvaluatedAt", Instant.now().toString());
    }
}
