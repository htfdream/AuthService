package com.gth.auth.domain.event;

import com.gth.auth.domain.vo.Email;
import com.gth.auth.domain.vo.UserId;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class UserLoggedInEvent implements DomainEvent{

    private final UUID eventId;
    private final Instant occurredOn;
    private final UserId userId;
    private final Email email;

    public UserLoggedInEvent(UserId userId, Email email) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.userId = userId;
        this.email = email;
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
        return "USER_LOGIN";
    }

    @Override
    public Optional<UserId> getUserId() {
        return Optional.of(userId);
    }
}
