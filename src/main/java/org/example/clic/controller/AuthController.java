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

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // login.html
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // register.html
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email ya registrado");
            return "register";
        }
        // Cifra la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Asigna el rol CLIENT, usando el enum anidado dentro de User
        user.setRole(User.Role.CLIENT);

        userService.save(user);

        // Redirige al login con un parámetro para mostrar mensaje si quieres
        return "redirect:/login?registered";
    }
}