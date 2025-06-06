package org.example.clic.service;

import org.example.clic.model.Event;
import org.example.clic.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll(); // Devuelve todos los eventos
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id); // Busca un evento por ID
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event); // Crea o actualiza un evento
    }

    @Override
    public void deleteById(Long id) {
        eventRepository.deleteById(id);// Elimina un evento por ID
    }

    @Override
    public boolean existsById(Long id) {
        return eventRepository.existsById(id); // Verifica si existe un evento
    }
    @Override
    public List<Event> findByCategory(String category) {
        // Si no hay categoría, devuelve todos los eventos
        if (category == null || category.isEmpty()) {
            return eventRepository.findAll();
        }
        return eventRepository.findByCategory(category);  // Filtra por categoría
    }

    @Override
    public List<Event> findByClientEmail(String email) {
        // Aquí deberías escribir la lógica para encontrar los eventos asociados al cliente por su email
        return eventRepository.findByClientEmail(email);
    }
}
