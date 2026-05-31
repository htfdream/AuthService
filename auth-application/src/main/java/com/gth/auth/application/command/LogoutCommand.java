package com.gth.auth.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class LogoutCommand implements Command {
    private String refresh_token;
}
