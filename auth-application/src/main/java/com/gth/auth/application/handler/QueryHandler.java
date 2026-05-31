package com.gth.auth.application.handler;

import com.gth.auth.application.query.Query;
import com.gth.auth.application.query.result.QueryResult;

public interface QueryHandler <C extends Query, R extends QueryResult> {
    public R handle(C query);
}
