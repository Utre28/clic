package org.example.clic.service;

import org.example.clic.model.Event;
import org.example.clic.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return eventRepository.existsById(id);
    }

    @Override
    public List<Event> findByCategory(String category) {
        return eventRepository.findByCategoryIgnoreCase(category);
    }

    @Override
    public List<Event> findPublicByCategory(String category) {
        return eventRepository.findByPrivadoFalseAndCategoryIgnoreCase(category);
    }

    @Override
    public List<Event> findTop5Public() {
        return eventRepository.findTop5ByPrivadoFalseOrderByIdDesc();
    }

    @Override
    public List<Event> findAllPublic() {
        return eventRepository.findByPrivadoFalse();
    }

    @Override
    public List<Event> findByClientId(Long id) {
        return eventRepository.findByClientId(id);
    }

    @Override
    public List<Event> findByPrivadoFalse() {
        return eventRepository.findByPrivadoFalse();
    }

    @Override
    public List<Event> findByPrivadoFalseAndCategory(String category) {
        return eventRepository.findByPrivadoFalseAndCategoryIgnoreCase(category);
    }
}
