package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.EventDTO;
import org.example.clic.mapper.EventMapper;
import org.example.clic.model.Event;
import org.example.clic.model.User;
import org.example.clic.service.EventService;
import org.example.clic.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.net.URI;
import java.util.List;
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
    public List<EventDTO> getAll(@RequestParam(required = false) String category) {
        List<Event> events;
        if (category == null || category.isEmpty()) {
            events = eventService.findAll();  // Obtiene todos los eventos
        } else {
            events = eventService.findByCategory(category);  // Filtra por categoría
        }
        return events.stream()
                .map(eventMapper::toDto)  // Convierte cada evento a un DTO
                .collect(Collectors.toList());
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

        Event event = new Event();
        event.setName(dto.getName());
        event.setDate(dto.getDate());
        event.setLocation(dto.getLocation());
        event.setCategory(dto.getCategory());
        if (clientOpt.isPresent()) {
            event.setClient(clientOpt.get()); // Asocia el evento con el cliente
        } else {
            event.setClient(null); // Evento sin cliente (portafolio)
        }

        Event saved = eventService.save(event); // Guarda el evento en la base de datos
        return ResponseEntity.created(URI.create("/api/events/" + saved.getId()))
                .body(eventMapper.toDto(saved)); // Retorna el evento guardado como respuesta
    }
}
