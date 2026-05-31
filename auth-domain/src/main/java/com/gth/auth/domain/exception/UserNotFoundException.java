package com.gth.auth.domain.exception;

import com.gth.auth.domain.vo.Email;
import com.gth.auth.domain.vo.UserId;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(Email email) {
        super("User with email not exists: " + email.value());
    }

    public UserNotFoundException(UserId userId) {
        super("User with userId not exists: " + userId.value());
    }
}
