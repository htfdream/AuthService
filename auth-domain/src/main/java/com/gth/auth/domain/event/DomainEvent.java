package com.gth.auth.domain.event;

import com.gth.auth.domain.vo.UserId;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface DomainEvent {
    UUID getEventId();
    Instant getOccurredOn();
    String getEventType();
    Optional<UserId> getUserId();
}
