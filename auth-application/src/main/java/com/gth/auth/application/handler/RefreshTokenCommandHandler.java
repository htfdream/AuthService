package com.gth.auth.application.handler;

import com.gth.auth.application.command.RefreshTokenCommand;
import com.gth.auth.application.command.result.RefreshTokenCommandResult;
import com.gth.auth.domain.aggregate.User;
import com.gth.auth.domain.exception.UserNotFoundException;
import com.gth.auth.domain.model.RefreshTokenData;
import com.gth.auth.domain.repository.UserRepository;
import com.gth.auth.domain.service.RefreshTokenService;
import com.gth.auth.domain.service.TokenGenerationService;
import com.gth.auth.domain.vo.UserId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RefreshTokenCommandHandler implements CommandHandler<RefreshTokenCommand, RefreshTokenCommandResult> {
    private final TokenGenerationService tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public RefreshTokenCommandHandler(
            TokenGenerationService tokenProvider,
            RefreshTokenService refreshTokenStore,
            UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenStore;
        this.userRepository = userRepository;
    }

    @Override
    public RefreshTokenCommandResult handle(RefreshTokenCommand command) {
        // 1. Проверить, что refresh token существует и не отозван
        if (!refreshTokenService.isValid(command.getRefreshToken())) {
            throw new RuntimeException("Invalid or revoked refresh token");
        }

        // 2. Получить данные токена
        RefreshTokenData tokenData = refreshTokenService.findByToken(command.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        // 3. Отозвать старый токен (rotation)
        refreshTokenService.revoke(command.getRefreshToken());

        // 4. Сгенерировать новую пару токенов
        UserId userId = new UserId(tokenData.userId());
        User user = userRepository.findById(userId).get();
        if (user == null) throw new UserNotFoundException(userId);
        var tokens = tokenProvider.generate(user);

        // 5. Сохранить новый refresh token
        RefreshTokenData newTokenData = RefreshTokenData.create(
                userId.value(),
                tokens.refreshToken(),
                tokenProvider.getRefreshTokenExpirationSeconds()
        );
        refreshTokenService.save(newTokenData);

        return new RefreshTokenCommandResult(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokenProvider.getAccessTokenExpirationSeconds()
        );
    }
}
