package com.gth.auth.application.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginCommand implements Command {
    private String email;
    private String password;

    public LoginCommand(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
