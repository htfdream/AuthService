package com.gth.auth.domain.service;

import com.gth.auth.domain.vo.Email;

import java.util.Optional;

public interface PasswordResetTokenService {
    public String generateToken(Email email);
    public Optional<Email> getEmailByToken(String token);
    public void deleteToken(String token);
}
