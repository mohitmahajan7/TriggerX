package org.stark.triggerxbackend.core.domain.port;

import org.stark.triggerxbackend.core.domain.model.trigger.Trigger;

import java.util.List;
import java.util.Optional;

public interface TriggerRepository {
    void save(Trigger trigger);

    Optional<Trigger> findById(String triggerId);

    List<Trigger> findActiveBySymbol(String symbol);


}
