package org.stark.triggerxbackend.auth.dto;

public record LoginResponse(
        boolean success,
        String message
) {
}
