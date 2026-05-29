package com.gth.auth.application.command.result;

import com.gth.auth.domain.vo.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginCommandResult implements CommandResult {
    /**
     * ID зарегистрированного пользователя
     */
    private final UserId userId;

    /**
     * JWT access token (живет 15 минут)
     */
    private final String accessToken;

    /**
     * Refresh token для обновления access token (живет 7 дней)
     */
    private final String refreshToken;

    /**
     * Время жизни access token в секундах
     */
    private final long expiresIn;

    /**
     * Email пользователя (опционально, для UI)
     */
    private final String email;

    /**
     * Имя пользователя (опционально, для UI)
     */
    private final String name;
}
