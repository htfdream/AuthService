package com.gth.auth.domain.vo;

import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) {
    private static final Pattern BASIC = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");
    private static final int MAX_LENGTH = 350;
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email can't be null or empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Too long email");
        }
        if (!value.matches(BASIC.pattern())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
    }

    public static Email of(String value) {
        return new Email(value);
    }
}
