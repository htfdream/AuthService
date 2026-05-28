package com.gth.auth.domain.exception;

public class AccountLockedException extends DomainException {
    public AccountLockedException() {
        super("Account is locked due to too many failed attempts");
    }

    public AccountLockedException(String message) {
        super(message);
    }
}