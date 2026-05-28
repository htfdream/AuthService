package com.gth.auth.domain.service;

import com.gth.auth.domain.vo.PasswordHash;
import com.gth.auth.domain.vo.UserId;

public interface PasswordHistoryService {
    boolean isPasswordReused(UserId userId, String rawPassword, PasswordEncoder encoder);
    void addToHistory(UserId userId, PasswordHash passwordHash);
}
