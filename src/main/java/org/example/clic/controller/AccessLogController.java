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
// Controlador REST para gestionar operaciones sobre registros de acceso
@RestController
@RequestMapping("/api/logs")
public class AccessLogController {
    private final AccessLogService logService;  // Servicio que implementa la lógica de negocio
    private final AccessLogMapper logMapper; // Mapper para convertir entre entidad y DTO
    // Inyección de dependencias mediante constructor
    public AccessLogController(AccessLogService logService, AccessLogMapper logMapper) {
        this.logService = logService;
        this.logMapper = logMapper;
    }
    // GET /api/logs  -> Devuelve todos los registros de acceso
    @GetMapping
    public List<AccessLogDTO> getAll() {
        return logService.findAll().stream()
                // Convertir cada entidad a DTO antes de devolverla
                .map(logMapper::toDto)
                .collect(Collectors.toList());
    }
    // GET /api/logs/{id}  -> Devuelve un registro por su ID
    @GetMapping("/{id}")
    public ResponseEntity<AccessLogDTO> getById(@PathVariable Long id) {
        // Buscar por ID y, si existe, mapear a DTO y devolver 200 OK; si no, 404 Not Found
        return logService.findById(id)
                .map(logMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // POST /api/logs  -> Crea un nuevo registro de acceso
    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody AccessLogDTO dto,// Validar propiedades del DTO según anotaciones
            BindingResult br) {  // Resultado de la validación
        // Si hay errores de validación, devolver 400 Bad Request con lista de mensajes
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        // Convertir DTO a entidad y guardarla
        var saved = logService.save(logMapper.toEntity(dto));
        // Convertir entidad guardada a DTO de salida
        var out = logMapper.toDto(saved);
        // Devolver 201 Created con ubicación del nuevo recurso y cuerpo con el DTO
        return ResponseEntity.created(URI.create("/api/logs/" + out.getId())).body(out);
    }
    // DELETE /api/logs/{id}  -> Elimina un registro por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Comprobar existencia antes de borrar
        if (logService.existsById(id)) {
            logService.deleteById(id);
            // 204 No Content si el borrado fue exitoso
            return ResponseEntity.noContent().build();
        }
        // 404 Not Found si no existe el registro
        return ResponseEntity.notFound().build();
    }
}