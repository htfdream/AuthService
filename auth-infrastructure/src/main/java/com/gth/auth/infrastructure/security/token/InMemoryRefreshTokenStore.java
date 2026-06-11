package com.gth.auth.infrastructure.security.token;

import com.gth.auth.domain.model.RefreshTokenData;
import com.gth.auth.domain.service.RefreshTokenService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("test")
public class InMemoryRefreshTokenStore  implements RefreshTokenService {

    private final Map<String, RefreshTokenData> tokens = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, RefreshTokenData>> userTokens = new ConcurrentHashMap<>();

    public void save(RefreshTokenData tokenData) {
        tokens.put(tokenData.token(), tokenData);

        userTokens.computeIfAbsent(tokenData.userId(), k -> new ConcurrentHashMap<>())
                .put(tokenData.token(), tokenData);
    }

    public Optional<RefreshTokenData> findByToken(String token) {
        return Optional.ofNullable(tokens.get(token));
    }

    public boolean isValid(String token) {
        RefreshTokenData data = tokens.get(token);
        return data != null && data.isValid();
    }

    public void revoke(String token) {
        RefreshTokenData oldData = tokens.get(token);
        if (oldData != null) {
            RefreshTokenData revokedData = oldData.revoke();
            tokens.put(token, revokedData);

            Map<String, RefreshTokenData> userTokenMap = userTokens.get(oldData.userId());
            if (userTokenMap != null) {
                userTokenMap.put(token, revokedData);
            }
        }
    }

    public void revokeAllForUser(UUID userId) {
        Map<String, RefreshTokenData> userTokenMap = userTokens.get(userId);
        if (userTokenMap != null) {
            userTokenMap.values().forEach(token -> {
                RefreshTokenData revoked = token.revoke();
                tokens.put(token.token(), revoked);
            });
            userTokenMap.clear();
        }
    }

    public void removeExpired() {
        tokens.entrySet().removeIf(entry -> !entry.getValue().isValid());
        userTokens.values().forEach(map ->
                map.entrySet().removeIf(entry -> !entry.getValue().isValid())
        );
    }
}
