package com.gth.auth.application.handler;

import com.gth.auth.application.command.Command;
import com.gth.auth.application.command.result.CommandResult;

public interface CommandHandler<C extends Command, R extends CommandResult> {
    public R handle(C cmd);
}
