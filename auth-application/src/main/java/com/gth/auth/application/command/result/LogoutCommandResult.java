package com.gth.auth.application.command.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class LogoutCommandResult implements CommandResult{
    private boolean success;
}
