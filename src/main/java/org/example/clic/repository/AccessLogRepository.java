package org.example.clic.repository;

import org.example.clic.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
// Repositorio JPA para operaciones CRUD de AccessLog
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
}
