package com.gth.auth.infrastructure.jpa.mapper;

import com.gth.auth.domain.aggregate.User;
import com.gth.auth.domain.vo.*;
import com.gth.auth.infrastructure.jpa.entity.UserJpaEntity;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {

    /**
     * Domain -> JPA Entity
     */
    public UserJpaEntity toEntity(User domain) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId().value());
        entity.setEmail(domain.getEmail().value());
        entity.setPasswordHash(domain.getPassword().value());
        entity.setName(domain.getName().value());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setLastLoginAt(domain.getLastLoginAt());
        entity.setActive(domain.isActive());
        return entity;
    }

    /**
     * JPA Entity -> Domain
     */
    public User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                UserId.fromUUID(entity.getId()),
                Email.of(entity.getEmail()),
                PasswordHash.fromHash(entity.getPasswordHash()),
                Name.of(entity.getName()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getLastLoginAt(),
                entity.isActive()
        );
    }
}
