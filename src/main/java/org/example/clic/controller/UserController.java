package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.UserDTO;
import org.example.clic.mapper.UserMapper;
import org.example.clic.model.User;
import org.example.clic.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService; // Servicio de usuarios
    private final UserMapper userMapper;   // Mapper entre User y UserDTO
    private final PasswordEncoder passwordEncoder; // Para encriptar contraseña

    public UserController(UserService userService, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Vistas MVC ---

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserDTO());
        return "registro"; // nombre de tu plantilla Thymeleaf para registro
    }

    // Procesar formulario de registro
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("userDto") UserDTO userDto,
                                      BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            return "registro"; // vuelve a mostrar el formulario con errores
        }

        if (userService.existsByEmail(userDto.getEmail())) {
            model.addAttribute("emailError", "El email ya está registrado");
            return "registro";
        }

        User user = userMapper.toEntity(userDto);

        // Asignar rol CLIENT por defecto
        user.setRole(User.Role.CLIENT);

        // Encriptar contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        userService.save(user);

        return "redirect:/login?registered"; // redirige a login con mensaje de éxito
    }

    // --- REST API ---

    @GetMapping("/api")
    @ResponseBody
    public List<UserDTO> getAllUsers() {
        return userService.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/me")
    @ResponseBody
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        return userService.findByGoogleId(principal.getName())
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/api")
    @ResponseBody
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

    @PutMapping("/api/{id}")
    @ResponseBody
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

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.existsById(id)) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/api/clients")
    @ResponseBody
    public List<UserDTO> getAllClients() {
        // Suponemos que UserService tiene método findByRole(Role role)
        return userService.findByRole(User.Role.CLIENT)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

}
