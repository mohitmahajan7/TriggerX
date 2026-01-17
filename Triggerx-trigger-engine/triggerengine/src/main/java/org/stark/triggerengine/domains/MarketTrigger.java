package org.stark.triggerengine.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.stark.triggerengine.enums.Conditions;
import org.stark.triggerengine.enums.TriggerStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "market_triggers",
        indexes = {
                @Index(name = "idx_trigger_symbol", columnList = "symbol"),
                @Index(name = "idx_trigger_user", columnList = "userId"),
                @Index(name = "idx_trigger_status", columnList = "status")
        })
@Getter
@Setter
public class MarketTrigger {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String userId;

    private String symbol;

    @Enumerated(EnumType.STRING)
    private Conditions condition;

    private double triggerPrice;

    @Enumerated(EnumType.STRING)
    private TriggerStatus status = TriggerStatus.ACTIVE;

    /**
     * If true → trigger fires only once
     */
    private boolean oneShot = true;

    /**
     * Cooldown period (seconds)
     */
    private int cooldownSeconds = 0;

    /**
     * Last time trigger fired
     */
    private Instant lastTriggeredAt;

    /**
     * Last evaluated price for crossing detection
     */
    private Double lastEvaluatedPrice;

    private Instant createdAt;

    private Instant updatedAt;

    /**
     * Optimistic locking
     */
    @Version
    private long version;

    // ================= LIFECYCLE =================

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
