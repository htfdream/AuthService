package com.gth.auth.domain.aggregate;

import com.gth.auth.domain.event.*;
import com.gth.auth.domain.exception.*;
import com.gth.auth.domain.service.PasswordEncoder;
import com.gth.auth.domain.service.PasswordHistoryService;
import com.gth.auth.domain.vo.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * User - главный Aggregate Root.
 * Содержит всю бизнес-логику, связанную с пользователем.
 *
 * Инварианты:
 * - Email должен быть валидным и уникальным
 * - Пароль должен быть сильным
 * - После 5 неудачных попыток аккаунт блокируется на 30 минут
 * - Нельзя использовать последние 5 паролей
 */
public final class User {
    // Identity
    private final UserId id;

    // Value Objects
    private final Email email;
    private PasswordHash password;
    private final Name name;

    // State
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;
    private boolean active;

    // Domain Events
    private final List<DomainEvent> events = new ArrayList<>();

    // Константы
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(30);
    private static final int PASSWORD_HISTORY_SIZE = 5;

    // Приватный конструктор - только через фабричные методы
    private User(UserId id, Email email, PasswordHash password, Name name, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.active = true;
    }

    /**
     * Фабричный метод для регистрации нового пользователя.
     * Вся валидация происходит здесь.
     */
    public static User register(Email email, String rawPassword, Name name, PasswordEncoder encoder) {
        // Валидация пароля
        PasswordHash passwordHash = PasswordHash.fromRaw(rawPassword, encoder);

        // Создание пользователя
        User user = new User(
                UserId.generate(),
                email,
                passwordHash,
                name,
                Instant.now()
        );

        // Добавление события
        user.addEvent(new UserRegisteredEvent(user.id, user.email, user.name.value()));

        return user;
    }

    /**
     * Фабричный метод для восстановления пользователя из БД.
     */
    public static User reconstitute(UserId id, Email email, PasswordHash password, Name name,
                                    Instant createdAt, Instant updatedAt, Instant lastLoginAt,
                                    boolean active) {
        User user = new User(id, email, password, name, createdAt);
        user.updatedAt = updatedAt;
        user.lastLoginAt = lastLoginAt;
        user.active = active;
        return user;
    }

    /**
     * Аутентификация пользователя.
     */
    public void authenticate(String rawPassword, PasswordEncoder encoder) {
        // Проверка активности
        if (!active) {
            throw new AccountLockedException("Account is deactivated");
        }

        // Проверка пароля
        if (!password.matches(rawPassword, encoder)) {
            throw new InvalidCredentialsException();
        }

        // Успешная аутентификация
        lastLoginAt = Instant.now();
        updatedAt = Instant.now();

        addEvent(new UserLoggedInEvent(id, email));
    }

    /**
     * Смена пароля.
     */
    public void changePassword(String oldRawPassword, String newRawPassword,
                               PasswordEncoder encoder, PasswordHistoryService historyService) {
        // Сначала аутентифицируемся со старым паролем
        authenticate(oldRawPassword, encoder);


        // Установка нового пароля
        PasswordHash newPasswordHash = PasswordHash.fromRaw(newRawPassword, encoder);
        this.password = newPasswordHash;
        this.updatedAt = Instant.now();

        // Добавление в историю
        historyService.addToHistory(id, newPasswordHash);

        // Событие
        addEvent(new PasswordChangedEvent(id));
    }

    /**
     * Сброс пароля (без проверки старого).
     */
    public void resetPassword(String newRawPassword, PasswordEncoder encoder,
                              PasswordHistoryService historyService) {
        if (!active) {
            throw new AccountLockedException("Account is deactivated");
        }


        // Установка нового пароля
        PasswordHash newPasswordHash = PasswordHash.fromRaw(newRawPassword, encoder);
        this.password = newPasswordHash;
        this.updatedAt = Instant.now();

        // Добавление в историю
        historyService.addToHistory(id, newPasswordHash);

        // Событие
        addEvent(new PasswordChangedEvent(id));
    }




    /**
     * Деактивация аккаунта.
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
        // addEvent(new AccountDeactivatedEvent(id));
    }

    /**
     * Добавление события.
     */
    private void addEvent(DomainEvent event) {
        events.add(event);
    }

    /**
     * Получение и очистка событий.
     */
    public List<DomainEvent> releaseEvents() {
        List<DomainEvent> released = new ArrayList<>(events);
        events.clear();
        return released;
    }

    // Геттеры (только для чтения!)
    public UserId getId() { return id; }
    public Email getEmail() { return email; }
    public PasswordHash getPassword() { return password; }
    public Name getName() { return name; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public boolean isActive() { return active; }
}