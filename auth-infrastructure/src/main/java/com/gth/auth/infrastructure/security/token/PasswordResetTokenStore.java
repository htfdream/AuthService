package com.gth.auth.infrastructure.security.token;

import com.gth.auth.domain.service.PasswordResetTokenService;
import com.gth.auth.domain.vo.Email;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class PasswordResetTokenStore implements PasswordResetTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(15);
    private static final String PREFIX = "reset:";

    public PasswordResetTokenStore(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateToken(Email email) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(PREFIX + token, email.value(), TTL);
        return token;
    }

    @Override
    public Optional<Email> getEmailByToken(String token) {
        String email = redisTemplate.opsForValue().get("reset:" + token);
        Email result = null;
        if (email != null) result = Email.of(email);
        return Optional.ofNullable(result);
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete(token);
    }
}
