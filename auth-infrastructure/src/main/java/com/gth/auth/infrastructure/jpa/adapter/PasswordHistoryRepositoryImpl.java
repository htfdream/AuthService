package com.gth.auth.infrastructure.jpa.adapter;
import com.gth.auth.domain.model.PasswordHistory;
import com.gth.auth.domain.repository.PasswordHistoryRepository;
import com.gth.auth.domain.vo.PasswordHash;
import com.gth.auth.domain.vo.UserId;
import com.gth.auth.infrastructure.jpa.entity.PasswordHistoryJpaEntity;
import com.gth.auth.infrastructure.jpa.repository.PasswordHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PasswordHistoryRepositoryImpl implements PasswordHistoryRepository {

    private final PasswordHistoryJpaRepository jpaRepository;
    private static final int HISTORY_SIZE = 5;

    @Override
    @Transactional
    public void save(PasswordHistory history) {
        PasswordHistoryJpaEntity entity = new PasswordHistoryJpaEntity();
        entity.setUserId(history.userId().value());
        entity.setPasswordHash(history.passwordHash().value());
        entity.setChangedAt(history.changedAt());
        jpaRepository.save(entity);

        // Оставляем только последние 5 записей
        deleteOldHistory(history.userId(), HISTORY_SIZE);
    }

    @Override
    public List<PasswordHistory> findLastNByUserId(UserId userId, int limit) {
        return jpaRepository.findLastN(userId.value(), PageRequest.of(0, limit))
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOldHistory(UserId userId, int keepCount) {
        jpaRepository.deleteOldHistory(userId.value(), keepCount);
    }

    private PasswordHistory toDomain(PasswordHistoryJpaEntity entity) {
        return new PasswordHistory(
                UserId.fromUUID(entity.getUserId()),
                PasswordHash.fromHash(entity.getPasswordHash()),
                entity.getChangedAt()
        );
    }
}
