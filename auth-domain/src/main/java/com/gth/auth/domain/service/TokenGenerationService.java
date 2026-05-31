package com.gth.auth.domain.service;

import com.gth.auth.domain.aggregate.User;
import com.gth.auth.domain.vo.Email;

import java.util.UUID;

public interface TokenGenerationService {

    record TokenPair(String accessToken, String refreshToken) {}

    TokenPair generate(User user);

    public long getRefreshTokenExpirationSeconds();

    public long getAccessTokenExpirationSeconds();
}
