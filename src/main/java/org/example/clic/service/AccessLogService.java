package org.example.clic.service;

import org.example.clic.model.AccessLog;
import org.example.clic.repository.AccessLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    public AccessLogService(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    public List<AccessLog> getAllAccessLogs() {
        return accessLogRepository.findAll();
    }

    public AccessLog getAccessLogById(Long id) {
        return accessLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AccessLog not found with id " + id));
    }

    public AccessLog createAccessLog(AccessLog accessLog) {
        return accessLogRepository.save(accessLog);
    }

    public void deleteAccessLog(Long id) {
        accessLogRepository.deleteById(id);
    }
}
