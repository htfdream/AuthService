package com.gth.auth.infrastructure.security.token;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(InMemoryRefreshTokenStore.class)
@EnableScheduling
public class TokenCleanupScheduler {

    private final InMemoryRefreshTokenStore tokenStore;

    public TokenCleanupScheduler(InMemoryRefreshTokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Scheduled(fixedRate = 3600000) // Каждый час
    public void cleanupExpiredTokens() {
        tokenStore.removeExpired();
        System.out.println("Cleaned up expired refresh tokens");
    }
}