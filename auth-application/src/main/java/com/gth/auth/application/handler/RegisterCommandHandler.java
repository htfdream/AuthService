package com.gth.auth.application.handler;

import com.gth.auth.application.command.RegisterCommand;
import com.gth.auth.application.command.result.RegisterCommandResult;
import com.gth.auth.domain.aggregate.User;
import com.gth.auth.domain.exception.EmailAlreadyExistsException;
import com.gth.auth.domain.repository.UserRepository;
import com.gth.auth.domain.service.PasswordEncoder;
import com.gth.auth.domain.vo.Email;
import com.gth.auth.domain.vo.Name;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class RegisterCommandHandler implements CommandHandler<RegisterCommand, RegisterCommandResult> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    //private final TokenGenerationService tokenService;
    //TODO private final EventPublisher eventPublisher;

    public RegisterCommandHandler(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
       // this.tokenService = tokenService;
    }

    @Override
    public RegisterCommandResult handle (RegisterCommand command) {
        Email email = Email.of(command.getEmail());
        Name name = Name.of(command.getName());

        if(userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = User.register(email, command.getPassword(), name, passwordEncoder);

        userRepository.save(user);

        //var tokens = tokenService.generate(email);

        //TODO publish event

        return RegisterCommandResult.builder()
                .userId(user.getId())
                .email(user.getEmail().value())
                .name(user.getName().value())
                .build();
    }

}
