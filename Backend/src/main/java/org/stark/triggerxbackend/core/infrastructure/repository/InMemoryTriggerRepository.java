package org.stark.triggerxbackend.core.infrastructure.repository;

import org.stark.triggerxbackend.core.domain.model.trigger.Trigger;
import org.stark.triggerxbackend.core.domain.model.trigger.TriggerState;
import org.stark.triggerxbackend.core.domain.port.TriggerRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class InMemoryTriggerRepository implements TriggerRepository {

    private final Map<String, Trigger> store = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public void save(Trigger trigger) {
        store.put(trigger.getId(), trigger);
    }

    @Override
    public Optional<Trigger> findById(String triggerId) {
        return Optional.ofNullable(store.get(triggerId));
    }

    @Override
    public List<Trigger> findActiveBySymbol(String symbol) {
        return store.values()
                .stream()
                .filter(t -> t.getState() == TriggerState.ACTIVE)
                .filter(t -> t.getSymbol().equals(symbol))
                .collect(Collectors.toList());
    }
}
