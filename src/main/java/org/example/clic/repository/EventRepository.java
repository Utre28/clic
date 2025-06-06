package org.example.clic.repository;

import org.example.clic.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
// Repositorio JPA para operaciones CRUD de Event
public interface EventRepository extends JpaRepository<Event, Long> {
    // Encuentra eventos por categoría
    List<Event> findByCategory(String category);
    // Método para obtener eventos por email del cliente
    List<Event> findByClientEmail(String email);
}