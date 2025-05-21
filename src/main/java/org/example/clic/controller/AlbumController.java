package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.AlbumDTO;
import org.example.clic.mapper.AlbumMapper;
import org.example.clic.model.Album;
import org.example.clic.model.Event;
import org.example.clic.service.AlbumService;
import org.example.clic.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    public AlbumController(AlbumService albumService, AlbumMapper albumMapper, EventService eventService) {
        this.albumService = albumService;
        this.albumMapper = albumMapper;
        this.eventService = eventService;
    }

    @GetMapping
    public List<AlbumDTO> getAll() {
        return albumService.findAll().stream()
                .map(albumMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDTO> getById(@PathVariable Long id) {
        return albumService.findById(id)
                .map(albumMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<AlbumDTO>> getAlbumsByEvent(@PathVariable Long eventId) {
        List<AlbumDTO> albums = albumService.findByEventId(eventId).stream()
                .map(albumMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(albums);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AlbumDTO dto, BindingResult br) {
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = eventService.findById(dto.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));

        Album album = new Album();
        album.setName(dto.getName());
        album.setEvent(event);
        album.setCreatedAt(LocalDateTime.now());

        Album saved = albumService.save(album);
        return ResponseEntity.created(URI.create("/api/albums/" + saved.getId()))
                .body(albumMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody AlbumDTO dto,
                                    BindingResult br) {
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        return albumService.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    // Opcional: actualizar event si tienes la entidad Event lista
                    // Event event = eventService.findById(dto.getEventId()).orElse(null);
                     //if (event != null) existing.setEvent(event);

                    var updated = albumService.save(existing);
                    return ResponseEntity.ok(albumMapper.toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (albumService.existsById(id)) {
            albumService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}