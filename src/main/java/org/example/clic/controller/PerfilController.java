package org.example.clic.controller;

import org.example.clic.model.User;
import org.example.clic.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PerfilController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public PerfilController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    private String getEmailFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.example.clic.security.CustomUserDetails) {
            return ((org.example.clic.security.CustomUserDetails) principal).getUser().getEmail();
        } else if (principal instanceof org.example.clic.security.CustomOidcUser) {
            return ((org.example.clic.security.CustomOidcUser) principal).getUser().getEmail();
        } else if (principal instanceof User) {
            return ((User) principal).getEmail();
        } else if (principal instanceof OAuth2User) {
            return (String) ((OAuth2User) principal).getAttribute("email");
        } else {
            return authentication.getName();
        }
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication authentication) {
        String email = getEmailFromAuth(authentication);
        if (email == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(
            @RequestParam String email,
            @RequestParam(required = false) String nuevaPassword,
            @RequestParam(required = false) String confirmarPassword,
            Model model,
            Authentication authentication
    ) {
        String actualEmail = getEmailFromAuth(authentication);
        if (actualEmail == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(actualEmail).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        boolean error = false;

        if (!user.getEmail().equals(email)) {
            if (userService.existsByEmail(email)) {
                model.addAttribute("emailError", "El email ya está en uso.");
                error = true;
            } else {
                user.setEmail(email);
            }
        }

        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            if (!nuevaPassword.equals(confirmarPassword)) {
                model.addAttribute("passwordError", "Las contraseñas no coinciden.");
                error = true;
            } else {
                user.setPassword(passwordEncoder.encode(nuevaPassword));
            }
        }

        if (error) {
            model.addAttribute("user", user);
            return "perfil";
        }

        userService.save(user);
        model.addAttribute("user", user);
        model.addAttribute("success", "Perfil actualizado correctamente.");
        return "perfil";
    }
}
