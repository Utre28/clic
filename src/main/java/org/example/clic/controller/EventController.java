package org.example.clic.controller;

import org.example.clic.dto.EventDTO;
import org.example.clic.mapper.EventMapper;
import org.example.clic.model.Event;
import org.example.clic.model.User;
import org.example.clic.service.EventService;
import org.example.clic.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
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
    public ResponseEntity<?> create(
            @RequestParam("name") String name,
            @RequestParam("date") String date,
            @RequestParam("location") String location,
            @RequestParam("category") String category,
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam("privado") boolean privado,
            @RequestParam("type") String type,
            @RequestParam(value = "eventCover", required = false) MultipartFile eventCover
    ) {
        List<String> validCategories = List.of("Bodas", "Bautizos", "Comuniones", "Retratos", "Conciertos");
        if (!validCategories.contains(category)) {
            return ResponseEntity.badRequest().body("Categoría de evento no válida");
        }

        Optional<User> clientOpt = Optional.empty();
        if (clientId != null) {
            clientOpt = userService.findById(clientId);
            if (clientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Cliente no encontrado");
            }
        }

        // Validación de unicidad del nombre del evento
        if (eventService.findByNameIgnoreCase(name).isPresent()) {
            return ResponseEntity.badRequest().body("Ya existe un evento con ese nombre. Elige otro nombre.");
        }

        Event event = new Event();
        event.setName(name);
        // Convierte el String a LocalDate
        event.setDate(LocalDate.parse(date));
        event.setLocation(location);
        event.setCategory(category);
        event.setPrivado(privado);
        if (clientOpt.isPresent()) {
            event.setClient(clientOpt.get());
        } else {
            event.setClient(null);
        }

        // Guardar la portada si se envía
        if (eventCover != null && !eventCover.isEmpty()) {
            try {
                String uploadsDir = "uploads/event-covers";
                File dir = new File(uploadsDir);
                if (!dir.exists()) dir.mkdirs();
                String original = eventCover.getOriginalFilename();
                String ext = "";
                if (original != null && original.lastIndexOf('.') > 0) {
                    ext = original.substring(original.lastIndexOf('.'));
                }
                String filename = "event_" + System.currentTimeMillis() + ext;
                Path filePath = Paths.get(uploadsDir, filename);
                Files.copy(eventCover.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                // Usa el método correcto para asignar la URL de portada
                event.setCoverUrl("event-covers/" + filename); // Cambia 'setCoverUrl' si tu método tiene otro nombre
            } catch (IOException ex) {
                return ResponseEntity.internalServerError().body("Error guardando la portada del evento");
            }
        }

        Event saved = eventService.save(event);
        return ResponseEntity.created(URI.create("/api/events/" + saved.getId()))
                .body(eventMapper.toDto(saved));
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
