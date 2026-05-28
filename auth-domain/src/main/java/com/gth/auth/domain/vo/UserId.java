package com.gth.auth.domain.vo;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        if( value == null) {
            throw new IllegalArgumentException("UserId can't be null");
        }
    }

    public static UserId generate(){
        return new UserId(UUID.randomUUID());
    }

    public static UserId fromUUID(UUID id) {
        return new UserId(id);
    }
}
