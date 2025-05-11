package org.example.clic.service;

import org.example.clic.model.AccessLog;
import java.util.List;
import java.util.Optional;

public interface AccessLogService {
    List<AccessLog> findAll();
    Optional<AccessLog> findById(Long id);
    AccessLog save(AccessLog log);
    void deleteById(Long id);
    boolean existsById(Long id);
}