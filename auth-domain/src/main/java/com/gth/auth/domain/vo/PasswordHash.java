package com.gth.auth.domain.vo;

import com.gth.auth.domain.service.PasswordEncoder;

public record PasswordHash(String value) {
    public PasswordHash {
        if(value == null || value.isBlank()) {
            throw new IllegalArgumentException("PasswordHash can't be null or empty");
        }
    }

    public static PasswordHash fromRaw(String rawPassword, PasswordEncoder encoder) {
        return new PasswordHash(encoder.encode(rawPassword));
    }

    public static PasswordHash fromHash(String passwordHash) {
        return new PasswordHash(passwordHash);
    }

    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, value);
    }
}
