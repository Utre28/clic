package org.example.clic.service;

import org.example.clic.model.User;
import org.example.clic.model.VerificationCode;
import org.example.clic.repository.VerificationCodeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    private final VerificationCodeRepository codeRepository;

    public VerificationCodeServiceImpl(VerificationCodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    @Override
    public VerificationCode createCode(User user) {
        String code = String.format("%06d", new Random().nextInt(999999));
        VerificationCode verificationCode = new VerificationCode(code, user, LocalDateTime.now().plusMinutes(15));
        return codeRepository.save(verificationCode);
    }

    @Override
    public Optional<VerificationCode> findByCode(String code) {
        return codeRepository.findByCode(code);
    }

    @Override
    public void deleteByCode(String code) {
        codeRepository.deleteByCode(code);
    }
}
