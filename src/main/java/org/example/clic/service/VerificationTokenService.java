package org.example.clic.service;

import org.example.clic.model.User;
import org.example.clic.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenService {
    VerificationToken createToken(User user);
    Optional<VerificationToken> findByToken(String token);
    void deleteByToken(String token);
}
