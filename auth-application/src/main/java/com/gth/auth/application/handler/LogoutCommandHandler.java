package com.gth.auth.application.handler;

import com.gth.auth.application.command.LogoutCommand;
import com.gth.auth.application.command.result.LogoutCommandResult;
import com.gth.auth.domain.repository.UserRepository;
import com.gth.auth.domain.service.RefreshTokenService;
import com.gth.auth.domain.service.TokenGenerationService;
import org.springframework.stereotype.Component;

@Component
public class LogoutCommandHandler implements CommandHandler<LogoutCommand, LogoutCommandResult>{
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final TokenGenerationService tokenService;

    public LogoutCommandHandler(RefreshTokenService refreshTokenService, UserRepository userRepository, TokenGenerationService tokenService) {
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Override
    public LogoutCommandResult handle(LogoutCommand cmd) {
        refreshTokenService.revoke(cmd.getRefresh_token());
        return new LogoutCommandResult(true);
    }
}
