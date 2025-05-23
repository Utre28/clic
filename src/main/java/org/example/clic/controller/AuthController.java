package org.example.clic.controller;


import org.example.clic.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.example.clic.model.User;


import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;// Servicio para gestión de usuarios
    private final PasswordEncoder passwordEncoder;// Cifrador de contraseñas

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        // Devuelve la vista login.html para que el usuario inicie sesión
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        // Añade un objeto User vacío al modelo para enlazar el formulario
        model.addAttribute("user", new User());
        // Devuelve la vista register.html para el formulario de registro
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        // Comprueba si el email ya está registrado
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            // Si existe, añade error al modelo y devuelve la misma vista
            model.addAttribute("error", "Email ya registrado");
            return "register";
        }
        // Cifra la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Asigna el rol CLIENT, usando el enum anidado dentro de User
        user.setRole(User.Role.CLIENT);
        // Guarda el usuario en la base de datos
        userService.save(user);

        // Redirige al login con un parámetro para mostrar mensaje si quieres
        return "redirect:/login?registered";
    }
}