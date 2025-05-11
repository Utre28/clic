package org.example.clic.service;

import org.example.clic.model.Event;
import java.util.List;
import java.util.Optional;

public interface EventService {
    List<Event> findAll();
    Optional<Event> findById(Long id);
    Event save(Event event);
    void deleteById(Long id);
    boolean existsById(Long id);
}