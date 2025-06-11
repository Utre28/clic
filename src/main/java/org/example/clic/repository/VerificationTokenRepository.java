package org.example.clic.repository;

import org.example.clic.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByToken(String token);
    Optional<VerificationToken> findByUserIdAndType(Long userId, VerificationToken.Type type);
    @Transactional
    void deleteByUserIdAndType(Long userId, VerificationToken.Type type);
}
