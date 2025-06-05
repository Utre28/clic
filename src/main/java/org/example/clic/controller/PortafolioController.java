package org.example.clic.controller;

import org.example.clic.model.Event;
import org.example.clic.service.EventService;
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
}