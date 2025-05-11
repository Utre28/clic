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

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

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
        var user = userMapper.toEntity(userDto);
        var saved = userService.save(user);
        var dto = userMapper.toDto(saved);
        return ResponseEntity
                .created(URI.create("/api/users/" + dto.getId()))
                .body(dto);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.existsById(id)) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
