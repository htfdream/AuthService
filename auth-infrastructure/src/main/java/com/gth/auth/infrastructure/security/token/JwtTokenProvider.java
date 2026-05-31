package com.gth.auth.infrastructure.security.token;

import com.gth.auth.domain.aggregate.User;
import com.gth.auth.domain.service.TokenGenerationService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider implements TokenGenerationService {

    private final SecretKey secretKey;
    private final long accessTokenExpirationSeconds;
    private final long refreshTokenExpirationSeconds;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-expiration-seconds:900}") long accessExpiration,
            @Value("${security.jwt.refresh-token-expiration-seconds:604800}") long refreshExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationSeconds = accessExpiration;
        this.refreshTokenExpirationSeconds = refreshExpiration;
    }

    public String generateAccessToken(UUID userId) {
        return generateToken(userId, accessTokenExpirationSeconds);
    }

    public String generateRefreshToken(UUID userId) {
        return generateToken(userId, refreshTokenExpirationSeconds);
    }

    private String generateToken(UUID userId, long expirationSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(secretKey)
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return UUID.fromString(subject);
    }

    public Date getExpirationFromToken(String token) {
        Date subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return subject;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }

    @Override
    public TokenPair generate(User user) {
        return new TokenPair(generateAccessToken(user.getId().value()), generateRefreshToken(user.getId().value()));
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationSeconds;
    }

    public UUID getUserIdFromRefreshToken(String token) {
        return getUserIdFromToken(token); // JWT содержит userId
    }
}
