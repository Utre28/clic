package org.example.clic.service;

import org.example.clic.model.User;
import org.example.clic.model.VerificationCode;

import java.util.Optional;

public interface VerificationCodeService {
    VerificationCode createCode(User user);
    Optional<VerificationCode> findByCode(String code);
    void deleteByCode(String code);
}
