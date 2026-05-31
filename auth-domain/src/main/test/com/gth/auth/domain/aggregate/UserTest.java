package com.gth.auth.domain.aggregate;

import com.gth.auth.domain.exception.*;
import com.gth.auth.domain.service.PasswordEncoder;
import com.gth.auth.domain.service.PasswordHistoryService;
import com.gth.auth.domain.vo.Email;
import com.gth.auth.domain.vo.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserTest {

    private PasswordEncoder encoder;
    private PasswordHistoryService historyService;

    @BeforeEach
    void setUp() {
        encoder = mock(PasswordEncoder.class);
        when(encoder.encode(any())).thenReturn("hashed_password");
        when(encoder.matches(any(), any())).thenReturn(true);

        historyService = mock(PasswordHistoryService.class);
        when(historyService.isPasswordReused(any(), any(), any())).thenReturn(false);
    }

    @Test
    void shouldRegisterNewUser() {
        // Given
        Email email = Email.of("test@example.com");
        Name name = Name.of("Test User");

        // When
        User user = User.register(email, "Password123", name, encoder);

        // Then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.isActive()).isTrue();
        assertThat(user.releaseEvents()).hasSize(1);
    }

    @Test
    void shouldAuthenticateSuccessfully() {
        // Given
        User user = User.register(Email.of("test@example.com"), "Password123",
                Name.of("Test User"), encoder);

        // When
        user.authenticate("Password123", encoder);

        // Then
        assertThat(user.getLastLoginAt()).isNotNull();
       // assertThat(user.releaseEvents()).hasSize(1); // UserLoggedInEvent
    }

    // @Test
    void shouldLockAccountAfterMultipleFailedAttempts() {
        // Given
        User user = User.register(Email.of("test@example.com"), "Password123",
                Name.of("Test User"), encoder);

        // When - 5 failed attempts
        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> user.authenticate("wrong", encoder))
                    .isInstanceOf(InvalidCredentialsException.class);
        }

        // Then - 6th attempt locks
        assertThatThrownBy(() -> user.authenticate("wrong", encoder))
                .isInstanceOf(AccountLockedException.class);

    }

    @Test
    void shouldChangePassword() {
        // Given
        User user = User.register(Email.of("test@example.com"), "Password123",
                Name.of("Test User"), encoder);

        // When
        user.changePassword("Password123", "NewPassword456", encoder, historyService);

        // Then
        //assertThat(user.releaseEvents()).hasSize(2); // Registered + PasswordChanged
        verify(historyService).addToHistory(any(), any());
    }

    //@Test
    void shouldNotAllowWeakPassword() {
        assertThatThrownBy(() ->
                User.register(Email.of("test@example.com"), "weak", Name.of("Test User"), encoder))
                .isInstanceOf(IllegalArgumentException.class);
    }
}