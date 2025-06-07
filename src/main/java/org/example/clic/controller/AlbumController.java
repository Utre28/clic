package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.AlbumDTO;
import org.example.clic.mapper.AlbumMapper;
import org.example.clic.model.Album;
import org.example.clic.model.Event;
import org.example.clic.model.User;
import org.example.clic.service.AlbumService;
import org.example.clic.service.EventService;
import org.example.clic.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    private final AlbumService albumService;
    private final AlbumMapper albumMapper;
    private final EventService eventService;
    // Constructor con inyección de dependencias de servicios y mapper
    public AlbumController(AlbumService albumService, AlbumMapper albumMapper, EventService eventService) {
        this.albumService = albumService;
        this.albumMapper = albumMapper;
        this.eventService = eventService;
    }
    /**
     * GET /api/albums
     * Devuelve la lista completa de álbumes como DTOs
     */
    @GetMapping
    public List<AlbumDTO> getAll() {
        return albumService.findAll().stream()
                .map(albumMapper::toDto)
                .collect(Collectors.toList());
    }
    /**
     * GET /api/albums/{id}
     * Busca un álbum por ID
     * @param id Identificador del álbum
     * @return 200 OK con el DTO o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlbumDTO> getById(@PathVariable Long id) {
        return albumService.findById(id)
                .map(albumMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * GET /api/albums/by-event/{eventId}
     * Obtiene todos los álbumes asociados a un evento
     * @param eventId ID del evento
     */
    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<AlbumDTO>> getAlbumsByEvent(@PathVariable Long eventId) {
        List<AlbumDTO> albums = albumService.findByEventId(eventId).stream()
                .map(albumMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(albums);
    }
    /**
     * POST /api/albums
     * Crea un nuevo álbum
     * - Valida el DTO
     * - Comprueba que el evento existe
     * - Asigna fecha de creación y guarda
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AlbumDTO dto, BindingResult br) {
        // Manejo de errores de validación
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        // Obtener la entidad Event o lanzar 404 si no existe
        Event event = eventService.findById(dto.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
        // Crear entidad Album a partir del DTO
        Album album = new Album();
        album.setName(dto.getName());
        album.setEvent(event);
        album.setCreatedAt(LocalDateTime.now());  // Fecha de creación actual

        // Guardar y devolver 201 Created con la ubicación del nuevo recurso
        Album saved = albumService.save(album);
        return ResponseEntity.created(URI.create("/api/albums/" + saved.getId()))
                .body(albumMapper.toDto(saved));
    }
    /**
     * PUT /api/albums/{id}
     * Actualiza un álbum existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody AlbumDTO dto,
                                    BindingResult br) {
        // Validación del DTO
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        // Buscar el álbum existente y actualizar campos
        return albumService.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    // Si se desea actualizar el evento, descomentar:
                    // var event = eventService.findById(dto.getEventId()).orElse(null);
                    // if (event != null) existing.setEvent(event);

                    var updated = albumService.save(existing);
                    return ResponseEntity.ok(albumMapper.toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * DELETE /api/albums/{id}
     * Elimina un álbum por su ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (albumService.existsById(id)) {
            albumService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/my-albums")
    public ResponseEntity<List<AlbumDTO>> getMyAlbums(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Obtener todos los eventos del usuario
        List<Event> events = eventService.findByClientId(user.getId());

        // Obtener álbumes solo de los eventos de ese usuario
        List<Album> albums = events.stream()
                .flatMap(event -> albumService.findByEventId(event.getId()).stream())
                .collect(Collectors.toList());

        return ResponseEntity.ok(albums.stream()
                .map(albumMapper::toDto)
                .collect(Collectors.toList()));
    }
}

@Controller
@RequestMapping("/albumes")
class AlbumControllerMvc {
    private final AlbumService albumService;
    public AlbumControllerMvc(AlbumService albumService) {
        this.albumService = albumService;
    }
    @GetMapping("/by-event/{eventId}")
    public String viewAlbumsByEvent(@PathVariable Long eventId, Model model) {
        var albums = albumService.findByEventId(eventId);
        model.addAttribute("albums", albums);
        return "albumes-by-event";
    }
}

