package com.gth.auth.application.handler;

import com.gth.auth.application.command.PasswordResetCommand;
import com.gth.auth.application.command.result.PasswordResetCommandResult;
import com.gth.auth.domain.repository.UserRepository;
import com.gth.auth.domain.service.EmailService;
import com.gth.auth.domain.service.PasswordResetTokenService;
import com.gth.auth.domain.vo.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PasswordResetCommandHandler implements CommandHandler<PasswordResetCommand, PasswordResetCommandResult>{
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordResetTokenService tokenService;

    public PasswordResetCommandHandler(UserRepository userRepository, EmailService emailService, PasswordResetTokenService tokenService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.tokenService = tokenService;
    }

    @Override
    public PasswordResetCommandResult handle(PasswordResetCommand cmd) {
        Email email = Email.of(cmd.getEmail());
        return  userRepository.findByEmail(email).map( user -> {
            String token = tokenService.generateToken(email);
            emailService.sendPasswordResetEmail(email.value(), token, user.getName().value());
            log.info("Password reset token sent to: {}", email.value());
            return new PasswordResetCommandResult(true, "Reset link sent on email");
        }).orElseGet(() -> {
            log.info("Password reset requested for non-existent email: {}", email.value());
            return new PasswordResetCommandResult(true, "Reset link sent on email");
        });
    }
}
