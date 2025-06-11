package org.example.clic.service;

import org.example.clic.model.User;
import org.example.clic.model.VerificationToken;
import org.example.clic.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepository tokenRepository;

    public VerificationTokenServiceImpl(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public VerificationToken createToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user, LocalDateTime.now().plusDays(1));
        return tokenRepository.save(verificationToken);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    public VerificationToken createToken(User user, String code, VerificationToken.Type type, int expiryMinutes) {
        VerificationToken token = new VerificationToken(code, user, java.time.LocalDateTime.now().plusMinutes(expiryMinutes));
        token.setType(type);
        return tokenRepository.save(token);
    }

    @Override
    public Optional<VerificationToken> findByUserAndType(User user, VerificationToken.Type type) {
        return tokenRepository.findByUserIdAndType(user.getId(), type);
    }

    @Override
    @Transactional
    public void deleteByUserAndType(User user, VerificationToken.Type type) {
        tokenRepository.deleteByUserIdAndType(user.getId(), type);
    }
}
