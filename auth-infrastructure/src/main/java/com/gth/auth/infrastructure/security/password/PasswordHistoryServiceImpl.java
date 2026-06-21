package com.gth.auth.infrastructure.security.password;
import com.gth.auth.domain.model.PasswordHistory;
import com.gth.auth.domain.repository.PasswordHistoryRepository;
import com.gth.auth.domain.service.PasswordEncoder;
import com.gth.auth.domain.service.PasswordHistoryService;
import com.gth.auth.domain.vo.PasswordHash;
import com.gth.auth.domain.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PasswordHistoryServiceImpl implements PasswordHistoryService {

    private final PasswordHistoryRepository historyRepository;
    private static final int HISTORY_SIZE = 5;

    @Override
    @Transactional
    public void addToHistory(UserId userId, PasswordHash passwordHash) {
        historyRepository.save(
                new PasswordHistory(userId, passwordHash, Instant.now())
        );
    }

    @Override
    public boolean isPasswordReused(UserId userId, String rawPassword, PasswordEncoder encoder) {
        return historyRepository.findLastNByUserId(userId, HISTORY_SIZE)
                .stream()
                .anyMatch(history -> encoder.matches(rawPassword, history.passwordHash().value()));
    }
}