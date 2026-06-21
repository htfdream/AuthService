package com.gth.auth.infrastructure.email;

import com.gth.auth.domain.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MockEmailService implements EmailService {

    @Override
    public void sendPasswordResetEmail(String email, String token, String name) {
        log.info("========================================");
        log.info("📧 PASSWORD RESET EMAIL");
        log.info("To: {}", email);
        log.info("Name: {}", name);
        log.info("Token: {}", token);
        log.info("Link: http://localhost:8080/reset?token={}", token);
        log.info("Expires in: 15 minutes");
        log.info("========================================");
    }
}
