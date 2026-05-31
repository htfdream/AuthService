package com.gth.auth.application.query;

import com.gth.auth.application.query.result.QueryResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ValidateTokenQuery implements Query {
    private String token;
}
