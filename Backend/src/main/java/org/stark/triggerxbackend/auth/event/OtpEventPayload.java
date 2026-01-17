package org.stark.triggerxbackend.auth.event;

public record OtpEventPayload(
        String type,
        String email,
        String otp,
        String purpose
) {}
