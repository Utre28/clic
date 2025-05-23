package org.example.clic.service;

import org.example.clic.model.AccessLog;
import java.util.List;
import java.util.Optional;

// Servicio para gestionar registros de acceso a fotos
public interface AccessLogService {
    List<AccessLog> findAll();            // Obtiene todos los registros de acceso
    Optional<AccessLog> findById(Long id); // Busca un registro por su ID
    AccessLog save(AccessLog log);         // Crea o actualiza un registro
    void deleteById(Long id);              // Elimina un registro por ID
    boolean existsById(Long id);           // Verifica existencia por ID
}
