package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.PhotoDTO;
import org.example.clic.mapper.PhotoMapper;
import org.example.clic.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    private final PhotoService photoService;
    private final PhotoMapper photoMapper;

    public PhotoController(PhotoService photoService, PhotoMapper photoMapper) {
        this.photoService = photoService;
        this.photoMapper = photoMapper;
    }

    @GetMapping
    public List<PhotoDTO> getAll() {
        return photoService.findAll().stream()
                .map(photoMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhotoDTO> getById(@PathVariable Long id) {
        return photoService.findById(id)
                .map(photoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PhotoDTO dto, BindingResult br) {
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        var saved = photoService.save(photoMapper.toEntity(dto));
        var out = photoMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/photos/" + out.getId())).body(out);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody PhotoDTO dto,
                                    BindingResult br) {
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        return photoService.findById(id)
                .map(existing -> {
                    var toUpdate = photoMapper.toEntity(dto);
                    toUpdate.setId(id);
                    var updated = photoService.save(toUpdate);
                    return ResponseEntity.ok(photoMapper.toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (photoService.existsById(id)) {
            photoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}