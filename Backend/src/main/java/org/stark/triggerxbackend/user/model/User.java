package org.stark.triggerxbackend.user.model;

import java.time.Instant;
import java.util.UUID;

public class User {


    private final String id;
    private final String email;
    private final String passwordHash;
    private final Instant createdAt;

    public User(String email, String passwordHash) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }

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
