package org.example.clic.service;

import org.example.clic.model.Event;
import java.util.List;
import java.util.Optional;

// Servicio para gestionar operaciones de Event
public interface EventService {
    List<Event> findAll();    // Obtener todos los eventos
    Optional<Event> findById(Long id);  // Buscar evento por ID
    Event save(Event event);  // Crear o actualizar evento
    void deleteById(Long id);// Eliminar evento por ID
    boolean existsById(Long id);// Verificar existencia de evento
    List<Event> findByCategory(String category); // Filtrar eventos por categoría
    List<Event> findPublicByCategory(String category);
    List<Event> findTop5Public();
    List<Event> findAllPublic();

    List<Event> findByClientId(Long id);
    List<Event> findByPrivadoFalse();
    List<Event> findByPrivadoFalseAndCategory(String category);
}