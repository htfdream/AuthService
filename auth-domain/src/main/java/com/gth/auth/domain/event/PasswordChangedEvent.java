package com.gth.auth.domain.event;

import com.gth.auth.domain.vo.UserId;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public final class PasswordChangedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final UserId userId;

    public PasswordChangedEvent(UserId userId) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.userId = userId;
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "PASSWORD_CHANGED";
    }

    @Override
    public Optional<UserId> getUserId() {
        return Optional.of(userId);
    }
}
