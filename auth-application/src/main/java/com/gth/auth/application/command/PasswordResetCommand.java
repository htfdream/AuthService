package com.gth.auth.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PasswordResetCommand implements Command {
    private String email;
}
