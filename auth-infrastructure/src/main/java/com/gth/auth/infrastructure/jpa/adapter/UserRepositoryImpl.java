package com.gth.auth.infrastructure.jpa.adapter;

import com.gth.auth.domain.aggregate.User;
import com.gth.auth.domain.repository.UserRepository;
import com.gth.auth.domain.vo.Email;
import com.gth.auth.domain.vo.UserId;
import com.gth.auth.infrastructure.jpa.entity.UserJpaEntity;
import com.gth.auth.infrastructure.jpa.mapper.UserMapper;
import com.gth.auth.infrastructure.jpa.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public User save(User user) {
        log.debug("Saving user: {}", user.getEmail().value());

        UserJpaEntity entity = mapper.toEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UserId id) {
        log.debug("Finding user by id: {}", id.value());

        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        log.debug("Finding user by email: {}", email.value());

        return jpaRepository.findByEmail(email.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    @Transactional
    public void delete(User user) {
        log.debug("Deleting user: {}", user.getEmail().value());

        jpaRepository.deleteById(user.getId().value());
    }

}
