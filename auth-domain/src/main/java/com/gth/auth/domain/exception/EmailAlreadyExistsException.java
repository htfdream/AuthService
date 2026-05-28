package com.gth.auth.domain.exception;

import com.gth.auth.domain.vo.Email;

public class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException(Email email) {
        super("Email already exists: " + email.value());
    }
}
