package com.gth.auth.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ResetPasswordCommand implements Command{
    private String resetToken;
    private String newPassword;
}
