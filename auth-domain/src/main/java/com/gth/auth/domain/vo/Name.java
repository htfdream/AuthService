package com.gth.auth.domain.vo;

public record Name(String value) {

    public Name {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Name can't be null");
        }
        if (!value.matches("^[a-zA-Z\\s\\-']+$")) {
            throw new IllegalArgumentException("Name contains illegal characters");
        }
        value = value.trim();
    }

    public static Name of(String value) {
        return new Name(value);
    }
}
