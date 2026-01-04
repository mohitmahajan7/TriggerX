package org.stark.triggerxbackend.user.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * JPA only
     */
    protected User() {
    }

    /**
     * Controlled creation
     */
    public User(String email, String passwordHash) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }

    // ---- getters only ----

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
