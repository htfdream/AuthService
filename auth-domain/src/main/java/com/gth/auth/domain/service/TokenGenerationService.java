package com.gth.auth.domain.service;

import com.gth.auth.domain.vo.Email;

public interface TokenGenerationService {

    record TokenPair(String accessToken, String refreshToken) {}

    TokenPair generate(Email email);
}
