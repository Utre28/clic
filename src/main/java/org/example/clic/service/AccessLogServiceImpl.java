package org.example.clic.service;

import org.example.clic.model.AccessLog;
import org.example.clic.repository.AccessLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Marca clase como servicio de Spring
@Transactional // Maneja transacciones automáticamente
public class AccessLogServiceImpl implements AccessLogService {
    private final AccessLogRepository accessLogRepository; // Repositorio de AccessLog

    public AccessLogServiceImpl(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    @Override
    public List<AccessLog> findAll() {
        return accessLogRepository.findAll(); // Devuelve todos los registros
    }

    @Override
    public Optional<AccessLog> findById(Long id) {
        return accessLogRepository.findById(id); // Busca por ID
    }

    @Override
    public AccessLog save(AccessLog log) {
        return accessLogRepository.save(log); // Inserta o actualiza
    }

    @Override
    public void deleteById(Long id) {
        accessLogRepository.deleteById(id); // Elimina por ID
    }

    @Override
    public boolean existsById(Long id) {
        return accessLogRepository.existsById(id);  // Verifica existencia
    }
}
