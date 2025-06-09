package org.example.clic.controller;

import org.example.clic.model.Event;
import org.example.clic.model.User;
import org.example.clic.service.EventService;
import org.example.clic.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {
    private final EventService eventService;
    private final UserService userService;

    public IndexController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/eventos")
    public String eventos() {
        return "eventos";
    }

    @GetMapping("/mis-eventos")
    public String getMyEventsPage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        boolean isClient = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(userService.getClientRole()));

        if (!isClient) {
            return "redirect:/login";
        }

        String email = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.example.clic.security.CustomUserDetails) {
            email = ((org.example.clic.security.CustomUserDetails) principal).getUser().getEmail();
        } else if (principal instanceof org.example.clic.security.CustomOidcUser) {
            email = ((org.example.clic.security.CustomOidcUser) principal).getUser().getEmail();
        } else if (principal instanceof User) {
            email = ((User) principal).getEmail();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttribute("email");
        } else {
            email = authentication.getName();
        }

        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            List<Event> events = eventService.findByClientId(user.getId());
            // Opcional: ordenar por fecha descendente
            // events.sort(Comparator.comparing(Event::getDate).reversed());
            if (events.isEmpty()) {
                model.addAttribute("message", "No tienes eventos disponibles.");
            }
            model.addAttribute("events", events);
        } catch (Exception e) {
            model.addAttribute("message", "Ocurrió un error al cargar los eventos.");
            e.printStackTrace();
        }
        return "mis-eventos";
    }

}
