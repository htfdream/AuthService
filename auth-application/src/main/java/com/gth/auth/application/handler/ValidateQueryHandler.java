package com.gth.auth.application.handler;

import com.gth.auth.application.query.ValidateTokenQuery;
import com.gth.auth.application.query.result.ValidateTokenQueryResult;
import com.gth.auth.domain.service.TokenGenerationService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class ValidateQueryHandler implements QueryHandler<ValidateTokenQuery, ValidateTokenQueryResult> {
    private final TokenGenerationService tokenService;

    public ValidateQueryHandler(TokenGenerationService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public ValidateTokenQueryResult handle(ValidateTokenQuery query) {
        String token = query.getToken();

        Date expires_at = tokenService.getExpirationFromToken(token);
        UUID userId = tokenService.getUserIdFromToken(token);
        boolean valid = tokenService.validateToken(token);

        return ValidateTokenQueryResult.builder()
                .valid(valid)
                .expires_at(expires_at.toInstant())
                .userId(userId.toString())
                .build();
    }
}
