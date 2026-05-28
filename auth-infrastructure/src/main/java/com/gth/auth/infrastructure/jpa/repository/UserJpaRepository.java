package com.gth.auth.infrastructure.jpa.repository;

import com.gth.auth.infrastructure.jpa.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE UserJpaEntity u SET u.failedAttempts = :attempts, u.lockedUntil = :lockedUntil WHERE u.id = :id")
    int updateFailedAttempts(@Param("id") UUID id,
                             @Param("attempts") int attempts,
                             @Param("lockedUntil") Instant lockedUntil);
}