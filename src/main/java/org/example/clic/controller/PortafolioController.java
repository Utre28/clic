package org.example.clic.controller;

import org.example.clic.model.Event;
import org.example.clic.service.EventService;
import org.example.clic.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PortafolioController {
    private final EventService eventService;
    private final UserService userService; // Añade el servicio de usuario

    public PortafolioController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/portafolio")
    public String portafolio(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String eventoCategoria,
            Model model
    ) {
        System.out.println("[PORTAFOLIO] INICIO controlador /portafolio");
        List<Event> filteredEvents;
        try {
            if (eventoCategoria != null && !eventoCategoria.isBlank()) {
                String categoria = eventoCategoria.trim();
                System.out.println("[PORTAFOLIO] Filtrando por categoría: '" + categoria + "'");
                filteredEvents = eventService.findByPrivadoFalseAndCategory(categoria);
            } else {
                System.out.println("[PORTAFOLIO] Sin categoría, mostrando todos los públicos");
                filteredEvents = eventService.findByPrivadoFalse();
            }
            if (filteredEvents == null) {
                System.out.println("[PORTAFOLIO] ERROR: filteredEvents es null");
                filteredEvents = List.of();
            }
            System.out.println("[PORTAFOLIO] Eventos encontrados tras filtro: " + filteredEvents.size());
            filteredEvents.forEach(ev -> System.out.println("[PORTAFOLIO] Evento: " + ev.getName()));
            model.addAttribute("filteredEvents", filteredEvents);
            model.addAttribute("q", q != null ? q : "");
            model.addAttribute("eventoCategoria", eventoCategoria != null ? eventoCategoria.trim() : "");
            System.out.println("[PORTAFOLIO] Retornando vista: portafolio");
            return "portafolio";
        } catch (Exception ex) {
            System.out.println("[PORTAFOLIO] EXCEPCIÓN: " + ex.getMessage());
            ex.printStackTrace();
            return "error";
        }
    }
}