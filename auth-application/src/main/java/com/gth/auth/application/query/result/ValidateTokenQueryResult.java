package com.gth.auth.application.query.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
@AllArgsConstructor
public class ValidateTokenQueryResult implements QueryResult{
    private boolean valid;
    private String userId;
    private Instant expires_at;
}
