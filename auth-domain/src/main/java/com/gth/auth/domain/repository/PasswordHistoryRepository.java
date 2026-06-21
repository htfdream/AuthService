package com.gth.auth.domain.repository;

import com.gth.auth.domain.model.PasswordHistory;
import com.gth.auth.domain.vo.UserId;

import java.util.List;

public interface PasswordHistoryRepository {
    public void save(PasswordHistory history);
    public List<PasswordHistory> findLastNByUserId(UserId userId, int limit);
    void deleteOldHistory(UserId userId, int keepCount);
}
