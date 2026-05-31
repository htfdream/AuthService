package com.gth.auth.domain.service;

import com.gth.auth.domain.model.RefreshTokenData;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {

    public void save(RefreshTokenData tokenData);
    public Optional<RefreshTokenData> findByToken(String token);
    public boolean isValid(String token);
    public void revoke(String token);
    public void revokeAllForUser(UUID userId);
}
