package org.stark.triggerxbackend.auth.event;

public record UserRegisteredEvent(
        String eventType,
        String email
) {}
