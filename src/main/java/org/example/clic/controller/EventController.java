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

@RestController// Define controlador REST
@RequestMapping("/api/events") // Ruta base para todos los endpoints de eventos
public class EventController {
    private final EventService eventService;// Servicio de eventos
    private final EventMapper eventMapper;// Mapper entre entidad y DTO

    // Inyección de dependencias vía constructor
    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }
    @GetMapping("/eventos")
    public String eventos() {
        return "eventos";  // nombre del archivo Thymeleaf: eventos.html
    }


    /**
     * Obtiene todos los eventos, opcionalmente filtrados por categoría.
     * @param category Categoría para filtrar (opcional).
     * @return Lista de EventDTO.
     */
    @GetMapping
    public List<EventDTO> getAll(@RequestParam(required = false) String category) {
        List<Event> events;
        if (category == null || category.isEmpty()) {
            // Sin filtro de categoría
            events = eventService.findAll();
        } else {
            // Filtra por categoría específica
            events = eventService.findByCategory(category);
        }
        // Mapea cada Event a EventDTO y devuelve la lista
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }
    /**
     * Busca un evento por su ID.
     * @param id Identificador del evento.
     * @return 200 OK con EventDTO o 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable Long id) {
        return eventService.findById(id)
                .map(eventMapper::toDto)// Convierte entidad a DTO
                .map(ResponseEntity::ok) // Envía 200 OK
                .orElse(ResponseEntity.notFound().build()); // 404 si no existe
    }
    /**
     * Crea un nuevo evento.
     * @param dto Datos de entrada validados.
     * @param br Resultado de la validación.
     * @return 201 Created con EventDTO o 400 Bad Request en errores.
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EventDTO dto, BindingResult br) {
        if (br.hasErrors()) {
            // Recoge mensajes de error de cada campo y devuelve Bad Request
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        // Construye entidad Event a partir de DTO
        Event event = new Event();
        event.setName(dto.getName());
        event.setDate(dto.getDate());
        event.setLocation(dto.getLocation());

        // Guarda el evento y prepara la respuesta
        Event saved = eventService.save(event);
        return ResponseEntity.created(URI.create("/api/events/" + saved.getId()))
                .body(eventMapper.toDto(saved));
    }
    /**
     * Actualiza un evento existente.
     * @param id ID del evento a actualizar.
     * @param dto Datos de actualización validados.
     * @param br Resultado de la validación.
     * @return 200 OK con EventDTO actualizado o 404/400 según corresponda.
     */
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
        // Busca el evento y, si existe, aplica cambios
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

    /**
     * Elimina un evento por su ID.
     * @param id ID del evento.
     * @return 204 No Content si se borra o 404 Not Found si no existe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (eventService.existsById(id)) {
            // Borra y devuelve No Content
            eventService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        // Si no existe, devuelve 404
        return ResponseEntity.notFound().build();
    }
}