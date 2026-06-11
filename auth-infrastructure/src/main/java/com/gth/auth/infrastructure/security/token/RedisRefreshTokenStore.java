package com.gth.auth.infrastructure.security.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gth.auth.domain.model.RefreshTokenData;
import com.gth.auth.domain.service.RefreshTokenService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Profile("prod")
public class RedisRefreshTokenStore implements RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisRefreshTokenStore(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public void save(RefreshTokenData token) {
        String key = "refresh:" + token.token();
        try {
            String value = objectMapper.writeValueAsString(token);
            long ttl = Duration.between(Instant.now(), token.expiresAt()).getSeconds();
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
            redisTemplate.opsForSet().add("user_refresh:" + token.userId(), token.token());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<RefreshTokenData> findByToken(String token) {
        String key = "refresh:" + token;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(value, RefreshTokenData.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isValid(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("refresh:" + token));
    }

    @Override
    public void revoke(String token) {
        redisTemplate.delete("refresh:" + token);
    }

    @Override
    public void revokeAllForUser(UUID userId) {
        // Проще: хранить список токенов пользователя в Redis
        String userKey = "user_refresh:" + userId;
        Set<String> tokens = redisTemplate.opsForSet().members(userKey);
        if (tokens != null) {
            tokens.forEach(t -> redisTemplate.delete("refresh:" + t));
            redisTemplate.delete(userKey);
        }
    }
}
