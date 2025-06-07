package org.example.clic.controller;

import org.example.clic.mapper.EventMapper;
import org.example.clic.model.Event;
import org.example.clic.model.User;
import org.example.clic.service.AlbumService;
import org.example.clic.service.EventService;
import org.example.clic.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class IndexController {
    private final EventService eventService;
    private final UserService userService; // Agregado para inyectar el UserService
    private final AlbumService albumService; // Añade esto

    public IndexController(EventService eventService, UserService userService, AlbumService albumService) {
        this.albumService = albumService; // Inicializa el AlbumService
        this.eventService = eventService;
        this.userService = userService;
    }


    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Endpoint para mostrar todos los eventos
    @GetMapping("/eventos")
    public String eventos() {
        return "eventos";  // Nombre del archivo Thymeleaf: eventos.html
    }

    // Endpoint para mostrar los eventos del cliente
    @GetMapping("/mis-eventos")
    public String getMyEventsPage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        boolean isClient = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

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
            if (events.isEmpty()) {
                model.addAttribute("message", "No tienes eventos disponibles.");
            }
            model.addAttribute("events", events);
        } catch (Exception e) {
            model.addAttribute("message", "Ocurrió un error al cargar los eventos.");
            e.printStackTrace();
        }
        return "mis-eventos";  // Nombre del archivo Thymeleaf: mis-eventos.html
    }
    // Endpoint para que el fotógrafo vea todos los eventos y pueda buscar/filtrar
@GetMapping("/panel/eventos")
public String panelEventos(
        @RequestParam(value = "q", required = false) String query,
        @RequestParam(value = "eventoId", required = false) Long eventoId,
        Model model,
        Authentication authentication) {

    // Solo permitir acceso a fotógrafos
    boolean isPhotographer = authentication != null && authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_PHOTOGRAPHER"));
    if (!isPhotographer) {
        return "redirect:/login";
    }

    List<Event> events = eventService.findAll();

    // Top 5 eventos más recientes
    List<Event> topEvents = events.stream()
            .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
            .limit(5)
            .toList();
    model.addAttribute("topEvents", topEvents);

    // Filtrado
    List<Event> filteredEvents = events;
    if (eventoId != null) {
        filteredEvents = events.stream()
                .filter(e -> e.getId().equals(eventoId))
                .toList();
    } else if (query != null && !query.isBlank()) {
        filteredEvents = events.stream()
                .filter(e -> e.getName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
    model.addAttribute("filteredEvents", filteredEvents);

    return "panel-eventos";
}

// Endpoint para ver los álbumes de un evento (fotógrafo)
@GetMapping("/panel/eventos/{eventoId}")
public String verAlbumesEvento(@PathVariable Long eventoId, Model model, Authentication authentication) {
    boolean isPhotographer = authentication != null && authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_PHOTOGRAPHER"));
    if (!isPhotographer) {
        return "redirect:/login";
    }
    var evento = eventService.findById(eventoId).orElse(null);
    if (evento == null) {
        model.addAttribute("message", "Evento no encontrado");
        return "error";
    }
    var albumes = albumService.findByEventId(eventoId);
    model.addAttribute("evento", evento);
    model.addAttribute("albumes", albumes);
    return "panel-albumes";
}







}
