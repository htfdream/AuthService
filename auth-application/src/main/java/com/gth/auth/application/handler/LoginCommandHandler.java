package com.gth.auth.application.handler;

import com.gth.auth.application.command.LoginCommand;
import com.gth.auth.application.command.result.CommandResult;
import com.gth.auth.application.command.result.LoginCommandResult;
import com.gth.auth.domain.aggregate.User;
import com.gth.auth.domain.exception.UserNotFoundException;
import com.gth.auth.domain.model.RefreshTokenData;
import com.gth.auth.domain.repository.UserRepository;
import com.gth.auth.domain.service.PasswordEncoder;
import com.gth.auth.domain.service.RefreshTokenService;
import com.gth.auth.domain.service.TokenGenerationService;
import com.gth.auth.domain.vo.Email;
import org.springframework.stereotype.Component;

@Component
public class LoginCommandHandler implements CommandHandler<LoginCommand, CommandResult> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerationService tokenService;
    private final RefreshTokenService refreshTokenService;
    //TODO private final EventPublisher eventPublisher;

    public LoginCommandHandler(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenGenerationService tokenService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public LoginCommandResult handle(LoginCommand command) {
        Email email = Email.of(command.getEmail());

        if(!userRepository.existsByEmail(email)) {
            throw new UserNotFoundException(email);
        }

        User user = userRepository.findByEmail(email).get();

        user.authenticate(command.getPassword(), passwordEncoder);

        var tokens = tokenService.generate(user);

        RefreshTokenData tokenData = RefreshTokenData.create(
                user.getId().value(),
                tokens.refreshToken(),
                tokenService.getRefreshTokenExpirationSeconds()
        );
        refreshTokenService.save(tokenData);

        //TODO publish event

        return LoginCommandResult.builder()
                .userId(user.getId())
                .email(user.getEmail().value())
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .expiresIn(tokenService.getAccessTokenExpirationSeconds())
                .name(user.getName().value())
                .build();
    }
}
