package org.example.clic.controller;

import org.example.clic.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PerfilController {

    private final UserService userService;

    public PerfilController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/perfil")
    public String perfilPage(@AuthenticationPrincipal OidcUser principal, Model model) {
        if (principal != null) {
            // Aquí puedes obtener datos del usuario autenticado
            String email = principal.getEmail();
            // Puedes buscar el usuario en tu base de datos si quieres más información
            model.addAttribute("usuario", userService.findByEmail(email).orElse(null));
        }
        return "perfil"; // thymeleaf template: perfil.html
    }
}
