package org.stark.triggerxbackend.auth.dto;

public record LoginTokenResponse(
        String token,
        String email
) {}
