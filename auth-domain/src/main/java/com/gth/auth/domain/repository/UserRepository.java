package com.gth.auth.domain.repository;

import com.gth.auth.domain.aggregate.User;
import com.gth.auth.domain.vo.Email;
import com.gth.auth.domain.vo.UserId;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
    void delete(User user);
}
