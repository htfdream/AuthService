package com.gth.auth.application.command.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class RefreshTokenCommandResult implements CommandResult {
    private String accessToken;
    private String refreshToken;
    private Long expireIn;
}
