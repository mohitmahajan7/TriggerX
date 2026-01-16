package org.stark.triggerxbackend.auth.event;

public record OtpEventPayload(
        String eventType,
        String email,
        String otp,
        String purpose
) {}
