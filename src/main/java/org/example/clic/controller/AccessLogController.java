package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.AccessLogDTO;
import org.example.clic.mapper.AccessLogMapper;
import org.example.clic.service.AccessLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class AccessLogController {
    private final AccessLogService logService;
    private final AccessLogMapper logMapper;

    public AccessLogController(AccessLogService logService, AccessLogMapper logMapper) {
        this.logService = logService;
        this.logMapper = logMapper;
    }

    @GetMapping
    public List<AccessLogDTO> getAll() {
        return logService.findAll().stream()
                .map(logMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccessLogDTO> getById(@PathVariable Long id) {
        return logService.findById(id)
                .map(logMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AccessLogDTO dto, BindingResult br) {
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        var saved = logService.save(logMapper.toEntity(dto));
        var out = logMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/logs/" + out.getId())).body(out);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (logService.existsById(id)) {
            logService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}