package com.gth.auth.application.handler;

import com.gth.auth.application.command.ResetPasswordCommand;
import com.gth.auth.application.command.result.RefreshTokenCommandResult;
import com.gth.auth.application.command.result.ResetPasswordCommandResult;
import com.gth.auth.domain.aggregate.User;
import com.gth.auth.domain.repository.UserRepository;
import com.gth.auth.domain.service.PasswordEncoder;
import com.gth.auth.domain.service.PasswordHistoryService;
import com.gth.auth.domain.service.PasswordResetTokenService;
import com.gth.auth.domain.vo.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResetPasswordCommandHandler implements CommandHandler<ResetPasswordCommand, ResetPasswordCommandResult> {
    private final PasswordResetTokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryService passwordHistoryService;

    public ResetPasswordCommandHandler(PasswordResetTokenService tokenService, UserRepository userRepository, PasswordEncoder passwordEncoder, PasswordHistoryService passwordHistoryService) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordHistoryService = passwordHistoryService;
    }

    @Override
    public ResetPasswordCommandResult handle(ResetPasswordCommand cmd) {
        log.info("Start handle");
        Email email = tokenService.getEmailByToken(cmd.getResetToken()).orElseThrow(() -> new RuntimeException("Invalid reset token"));
        log.info("email: " + email.value());
        tokenService.deleteToken(cmd.getResetToken());
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.resetPassword(cmd.getNewPassword(), passwordEncoder, passwordHistoryService);
        userRepository.save(user);
        log.info("Password reset successfully for: {}", email.value());
        return new ResetPasswordCommandResult(true, "Password reset successfully");
    }
}
