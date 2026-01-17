package org.stark.triggerengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stark.triggerengine.domains.MarketTrigger;
import org.stark.triggerengine.enums.TriggerStatus;

import java.util.List;
import java.util.UUID;

public interface MarketTriggerRepository extends JpaRepository<MarketTrigger, UUID> {

    List<MarketTrigger> findBySymbolAndStatus(String symbol, TriggerStatus status);

    List<MarketTrigger> findByUserIdAndStatus(String userId, TriggerStatus status);

    //  engine-critical query
    List<MarketTrigger> findBySymbolAndStatusIn(String symbol, List<TriggerStatus> statuses);
}
