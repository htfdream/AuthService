package com.gth.auth.domain.service;

public interface EmailService {
    void sendPasswordResetEmail(String email, String token, String name);
}
