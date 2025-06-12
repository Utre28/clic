package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.EventDTO;
import org.example.clic.mapper.EventMapper;
import org.example.clic.model.Event;
import org.example.clic.model.User;
import org.example.clic.service.EventService;
import org.example.clic.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserService userService;

    public EventController(EventService eventService, EventMapper eventMapper, UserService userService) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
        this.userService = userService;
    }



    // Endpoint para obtener todos los eventos, opcionalmente filtrados por categoría
    @GetMapping
    public List<EventDTO> getAll(@RequestParam(required = false) String category, Authentication authentication) {
        boolean isPhotographer = authentication != null && authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(userService.getPhotographerRole()));
        boolean isClient = authentication != null && authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(userService.getClientRole()));
        List<Event> events;
        if (isPhotographer) {
            // Fotógrafo ve todos los eventos
            if (category == null || category.isEmpty()) {
                events = eventService.findAll();
            } else {
                events = eventService.findByCategory(category);
            }
        } else if (isClient) {
            // Cliente ve sus eventos
            String email = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                events = eventService.findByClientId(userOpt.get().getId());
            } else {
                events = List.of();
            }
        } else {
            // Público general solo ve eventos públicos
            if (category == null || category.isEmpty()) {
                events = eventService.findAllPublic();
            } else {
                events = eventService.findPublicByCategory(category);
            }
        }
        return events.stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    // Endpoint para crear un nuevo evento
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EventDTO dto, BindingResult br) {
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        List<String> validCategories = List.of("Bodas", "Bautizos", "Comuniones", "Retratos", "Conciertos");
        if (!validCategories.contains(dto.getCategory())) {
            return ResponseEntity.badRequest().body("Categoría de evento no válida");
        }

        Optional<User> clientOpt = Optional.empty();
        if (dto.getClientId() != null) {
            clientOpt = userService.findById(dto.getClientId());
            if (clientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Cliente no encontrado");
            }
        }

        // Validación de unicidad del nombre del evento
        if (eventService.findByNameIgnoreCase(dto.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Ya existe un evento con ese nombre. Elige otro nombre.");
        }

        Event event = new Event();
        event.setName(dto.getName());
        event.setDate(dto.getDate());
        event.setLocation(dto.getLocation());
        event.setCategory(dto.getCategory());
        event.setPrivado(dto.isPrivado());
        if (clientOpt.isPresent()) {
            event.setClient(clientOpt.get()); // Asocia el evento con el cliente
        } else {
            event.setClient(null); // Evento sin cliente (portafolio)
        }

        Event saved = eventService.save(event); // Guarda el evento en la base de datos
        return ResponseEntity.created(URI.create("/api/events/" + saved.getId()))
                .body(eventMapper.toDto(saved)); // Retorna el evento guardado como respuesta
    }

    @GetMapping("/stats")
    public List<Map<String, Object>> getEventStats() {
        // Suponiendo que Event tiene un campo 'visits'
        return eventService.findAll().stream()
            .map(ev -> {
                Map<String, Object> map = Map.of(
                    "id", ev.getId(),
                    "name", ev.getName(),
                    "visits", ev instanceof Event && ev.getClass().getDeclaredFields() != null && hasVisits(ev) ? getVisits(ev) : 0
                );
                return map;
            })
            .collect(Collectors.toList());
    }

    // Métodos auxiliares para obtener visitas si el campo existe
    private boolean hasVisits(Event ev) {
        try {
            ev.getClass().getDeclaredField("visits");
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
    private int getVisits(Event ev) {
        try {
            var f = ev.getClass().getDeclaredField("visits");
            f.setAccessible(true);
            return (int) f.get(ev);
        } catch (Exception e) {
            return 0;
        }
    }
}
