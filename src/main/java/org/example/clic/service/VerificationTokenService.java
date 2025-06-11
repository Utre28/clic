package org.example.clic.service;

import org.example.clic.model.User;
import org.example.clic.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenService {
    VerificationToken createToken(User user);
    VerificationToken createToken(User user, String code, VerificationToken.Type type, int expiryMinutes);
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUserAndType(User user, VerificationToken.Type type);
    void deleteByToken(String token);
    void deleteByUserAndType(User user, VerificationToken.Type type);
}
