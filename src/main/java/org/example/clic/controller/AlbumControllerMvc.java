package org.example.clic.controller;

import org.example.clic.service.AlbumService;
import org.example.clic.service.EventService;
import org.example.clic.service.UserService;
import org.example.clic.model.User;
import org.example.clic.security.CustomUserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/albumes")
public class AlbumControllerMvc {
    private final AlbumService albumService;
    private final EventService eventService;
    private final UserService userService;

    public AlbumControllerMvc(AlbumService albumService, EventService eventService, UserService userService) {
        this.albumService = albumService;
        this.eventService = eventService;
        this.userService = userService;
    }

    // Vista de álbumes por evento (público o desde portafolio)
    @GetMapping("/by-event/{eventId}")
    public String viewAlbumsByEvent(@PathVariable Long eventId, Model model) {
        var albums = albumService.findByEventId(eventId);
        var eventoOpt = eventService.findById(eventId);
        if (eventoOpt.isPresent()) {
            var evento = eventoOpt.get();
            evento.setVisits(evento.getVisits() + 1);
            eventService.save(evento);
            model.addAttribute("evento", evento);
        } else {
            model.addAttribute("evento", null);
        }
        model.addAttribute("albumes", albums);
        return "albumes-by-event";
    }

    // Vista de álbumes de "Mis eventos" (solo para usuarios autenticados)
    @GetMapping("/mis-eventos")
    public String viewMyAlbums(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("error", "Debes iniciar sesión para ver tus eventos.");
            return "redirect:/login";
        }
        String email = null;
        // Soporte para usuarios OAuth2 y login normal
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
            email = (String) oauth2User.getAttribute("email");
        } else {
            email = authentication.getName();
        }
        if (email == null) {
            model.addAttribute("error", "No se pudo obtener el email del usuario.");
            return "redirect:/login";
        }
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            // Si el usuario no existe (OAuth2), créalo automáticamente como CLIENT verificado
            user = new User();
            user.setEmail(email);
            user.setName(email.split("@")[0]);
            user.setRole(User.Role.CLIENT);
            user.setEmailVerified(true);
            user = userService.save(user);
        }
        if (user.getRole() != User.Role.CLIENT) {
            model.addAttribute("error", "Solo los clientes pueden ver sus eventos.");
            return "redirect:/";
        }
        var eventos = eventService.findByClientId(user.getId());
        if (eventos == null || eventos.isEmpty()) {
            model.addAttribute("info", "No tienes eventos asociados.");
        }
        var albumes = eventos.stream()
                .flatMap(evento -> albumService.findByEventId(evento.getId()).stream())
                .toList();
        model.addAttribute("albumes", albumes);
        model.addAttribute("eventos", eventos);
        return "panel-albumes";
    }
}
