package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.UserDTO;
import org.example.clic.mapper.UserMapper;
import org.example.clic.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService; // Servicio de usuarios
    private final UserMapper userMapper;// Mapper entre User y UserDTO

    // Inyección de dependencias vía constructor
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }
    /**
     * Lista todos los usuarios.
     * @return Lista de UserDTO.
     */
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.findAll()// Obtiene todas las entidades User
                .stream()
                .map(userMapper::toDto) // Convierte cada User a UserDTO
                .collect(Collectors.toList());// Recoge en lista
    }

    /**
     * Obtiene un usuario por ID.
     * @param id Identificador del usuario.
     * @return 200 OK con UserDTO o 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Devuelve los datos del usuario autenticado.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        // principal.getName() es el sub (googleId), así que buscamos por ese campo
        return userService.findByGoogleId(principal.getName())
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * Crea un nuevo usuario.
     * @param userDto DTO con datos del nuevo usuario.
     * @param result Resultado de la validación.
     * @return 201 Created con UserDTO o 400 Bad Request con errores.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    result.getFieldErrors()
                            .stream()
                            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                            .collect(Collectors.toList())
            );
        }
        // Convierte DTO a entidad, guarda y vuelve a mapear a DTO
        var user = userMapper.toEntity(userDto);
        var saved = userService.save(user);
        var dto = userMapper.toDto(saved);
        return ResponseEntity
                .created(URI.create("/api/users/" + dto.getId()))
                .body(dto);
    }

    /**
     * Actualiza un usuario existente.
     * @param id ID del usuario a actualizar.
     * @param userDto DTO con datos actualizados.
     * @param result Resultado de la validación.
     * @return 200 OK con UserDTO actualizado o 404/400 según corresponda.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody UserDTO userDto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    result.getFieldErrors()
                            .stream()
                            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                            .collect(Collectors.toList())
            );
        }
        return userService.findById(id)
                .map(existing -> {
                    var toUpdate = userMapper.toEntity(userDto);
                    toUpdate.setId(id);
                    var updated = userService.save(toUpdate);
                    return ResponseEntity.ok(userMapper.toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un usuario por su ID.
     * @param id ID del usuario.
     * @return 204 No Content o 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.existsById(id)) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
