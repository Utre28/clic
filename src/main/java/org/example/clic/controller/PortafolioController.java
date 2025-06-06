package org.example.clic.controller;

import org.example.clic.model.Event;
import org.example.clic.service.EventService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PortafolioController {
    private final EventService eventService;

    // Inyectamos el servicio de eventos
    public PortafolioController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/portafolio")
    public String portafolio(Model model) {
        // Obtener todos los eventos desde el servicio
        model.addAttribute("events", eventService.findAll()); // Aquí estamos agregando los eventos al modelo
        return "portafolio";  // Este es el nombre de la plantilla Thymeleaf que mostrará el portafolio
    }

    @GetMapping("/categoria")
    public String categoria(@RequestParam("event") String eventName, Model model) {
        // Aquí pasas el nombre del evento al modelo
        model.addAttribute("eventName", eventName); // Esto lo usas en la vista
        return "categoria"; // El nombre de la vista que procesará el evento
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto"; // Asegúrate de tener este archivo HTML
    }
 /*   @GetMapping("/mis-eventos")
    public String misEventos(@AuthenticationPrincipal User user, Model model) {
        // Obtener los eventos asociados al cliente
        List<Event> events = eventService.findByClientEmail(user.getUsername());

        // Pasar los eventos al modelo
        model.addAttribute("events", events);

        // Devolver la vista correspondiente
        return "mis-eventos";
    }*/
    @GetMapping("/mis-eventos")
    public String misEventos(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();  // Accede al nombre del usuario autenticado
            // Aquí puedes consultar los eventos relacionados con el usuario
            model.addAttribute("username", username);
            // Obtén los eventos del usuario, si es necesario
            return "mis-eventos";  // Retorna la vista correspondiente
        } else {
            return "redirect:/login";  // Si no está autenticado, redirige a login
        }
    }

}