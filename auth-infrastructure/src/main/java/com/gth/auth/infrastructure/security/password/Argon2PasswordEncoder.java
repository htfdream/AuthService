package com.gth.auth.infrastructure.security.password;

import com.gth.auth.domain.service.PasswordEncoder;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Argon2PasswordEncoder implements PasswordEncoder {

    private final Argon2 argon2;
    private final int iterations;
    private final int memory;
    private final int parallelism;

    public Argon2PasswordEncoder(
            @Value("${security.password.argon2.iterations:3}") int iterations,
            @Value("${security.password.argon2.memory:65536}") int memory,
            @Value("${security.password.argon2.parallelism:1}") int parallelism) {
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        this.iterations = iterations;
        this.memory = memory;
        this.parallelism = parallelism;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return argon2.hash(iterations, memory, parallelism, rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedHash) {
        return argon2.verify(encodedHash, rawPassword.toString());
    }
}
