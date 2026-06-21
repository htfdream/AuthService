package com.gth.auth.domain.model;

import com.gth.auth.domain.vo.PasswordHash;
import com.gth.auth.domain.vo.UserId;

import java.time.Instant;

public record PasswordHistory (
    UserId userId,
    PasswordHash passwordHash,
    Instant changedAt
) { }
