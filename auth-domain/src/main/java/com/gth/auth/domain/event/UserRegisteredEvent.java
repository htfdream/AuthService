package com.gth.auth.domain.event;

import com.gth.auth.domain.vo.Email;
import com.gth.auth.domain.vo.UserId;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class UserRegisteredEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final UserId userId;
    private final Email email;
    private final String name;

    public UserRegisteredEvent(UserId userId, Email email, String name) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.userId = userId;
        this.email = email;
        this.name = name;
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
        return "USER_REGISTERED";
    }

    @Override
    public Optional<UserId> getUserId() {
        return Optional.of(userId);
    }

    public UserId getCUserId() {
        return userId;
    }

    public Email getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
