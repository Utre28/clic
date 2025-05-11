package org.example.clic.service;

import org.example.clic.model.AccessLog;
import org.example.clic.repository.AccessLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccessLogServiceImpl implements AccessLogService {
    private final AccessLogRepository accessLogRepository;

    public AccessLogServiceImpl(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    @Override
    public List<AccessLog> findAll() {
        return accessLogRepository.findAll();
    }

    @Override
    public Optional<AccessLog> findById(Long id) {
        return accessLogRepository.findById(id);
    }

    @Override
    public AccessLog save(AccessLog log) {
        return accessLogRepository.save(log);
    }

    @Override
    public void deleteById(Long id) {
        accessLogRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return accessLogRepository.existsById(id);
    }
}
