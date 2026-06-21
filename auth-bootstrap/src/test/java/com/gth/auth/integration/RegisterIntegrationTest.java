package com.gth.auth.integration;

import com.gth.auth.application.command.RegisterCommand;
import com.gth.auth.application.command.result.RegisterCommandResult;
import com.gth.auth.application.handler.RegisterCommandHandler;
import com.gth.auth.domain.exception.EmailAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gth.auth.BaseIntegrationTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ActiveProfiles("test")
public class RegisterIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RegisterCommandHandler registerCommandHandler;

    @Test
    void shouldRegisterUser() {
        RegisterCommand command = new RegisterCommand("test@example.com", "Test User", "Password123");

        RegisterCommandResult result = registerCommandHandler.handle(command);

        assertThat(result.getUserId()).isNotNull();
        //assertThat(result.getAccessToken()).isNotBlank();
        //assertThat(result.getRefreshToken()).isNotBlank();
        //assertThat(result.getExpiresIn()).isGreaterThan(0);
    }

    @Test
    void shouldNotRegisterDuplicateEmail() {
        RegisterCommand command = new RegisterCommand(
                "duplicate@example.com",
                "Test User",
                "Password123"
        );

        registerCommandHandler.handle(command);

        assertThatThrownBy(() -> registerCommandHandler.handle(command))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }
}
