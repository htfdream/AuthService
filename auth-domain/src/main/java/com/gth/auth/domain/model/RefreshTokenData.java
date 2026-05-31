package com.gth.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenData(
        UUID userId,
        String token,
        Instant createdAt,
        Instant expiresAt,
        boolean revoked
) {
    public static RefreshTokenData create(UUID userId, String token, long ttlSeconds) {
        Instant now = Instant.now();
        return new RefreshTokenData(
                userId,
                token,
                now,
                now.plusSeconds(ttlSeconds),
                false
        );
    }

    public boolean isValid() {
        return !revoked && Instant.now().isBefore(expiresAt);
    }

    public RefreshTokenData revoke() {
        return new RefreshTokenData(userId, token, createdAt, expiresAt, true);
    }
}
