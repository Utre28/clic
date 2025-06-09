package org.example.clic.repository;

import org.example.clic.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
// Repositorio JPA para operaciones CRUD de Event
public interface EventRepository extends JpaRepository<Event, Long> {
    // Encuentra eventos por categoría
    List<Event> findByCategoryIgnoreCase(String category);
    List<Event> findByClientId(Long clientId);
    List<Event> findByPrivadoFalse();
    List<Event> findByPrivadoFalseAndCategoryIgnoreCase(String category);
    List<Event> findTop5ByPrivadoFalseOrderByIdDesc();
}