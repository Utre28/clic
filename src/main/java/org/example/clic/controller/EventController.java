package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.EventDTO;
import org.example.clic.mapper.EventMapper;
import org.example.clic.model.Event;
import org.example.clic.model.User;
import org.example.clic.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @GetMapping
    public List<EventDTO> getAll(@RequestParam(required = false) String category) {
        List<Event> events;
        if (category == null || category.isEmpty()) {
            events = eventService.findAll();
        } else {
            events = eventService.findByCategory(category);
        }
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable Long id) {
        return eventService.findById(id)
                .map(eventMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EventDTO dto, BindingResult br) {
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        Event event = new Event();
        event.setName(dto.getName());
        event.setDate(dto.getDate());
        event.setLocation(dto.getLocation());

        Event saved = eventService.save(event);
        return ResponseEntity.created(URI.create("/api/events/" + saved.getId()))
                .body(eventMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody EventDTO dto,
                                    BindingResult br) {
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        return eventService.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setDate(dto.getDate());
                    existing.setLocation(dto.getLocation());
                    var updated = eventService.save(existing);
                    return ResponseEntity.ok(eventMapper.toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (eventService.existsById(id)) {
            eventService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}