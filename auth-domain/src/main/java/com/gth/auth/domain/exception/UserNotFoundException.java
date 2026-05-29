package com.gth.auth.domain.exception;

import com.gth.auth.domain.vo.Email;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(Email email) {
        super("User with email not exists: " + email.value());
    }
}
