package com.gth.auth.infrastructure.jpa.repository;

import com.gth.auth.infrastructure.jpa.entity.PasswordHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface PasswordHistoryJpaRepository extends JpaRepository<PasswordHistoryJpaEntity, Long> {
    @Query("SELECT h FROM PasswordHistoryJpaEntity h WHERE h.userId = :userId ORDER BY h.changedAt DESC")
    List<PasswordHistoryJpaEntity> findLastN(@Param("userId") UUID userId, org.springframework.data.domain.Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM password_history WHERE user_id = :userId AND " +
            "id NOT IN (SELECT id FROM password_history WHERE user_id = :userId " +
            "ORDER BY changed_at DESC LIMIT :keepCount)", nativeQuery = true)
    void deleteOldHistory(@Param("userId") UUID userId, @Param("keepCount") int keepCount);
}
