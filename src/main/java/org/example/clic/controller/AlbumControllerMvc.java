package org.example.clic.controller;

import org.example.clic.service.AlbumService;
import org.example.clic.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/albumes")
public class AlbumControllerMvc {
    private final AlbumService albumService;
    private final EventService eventService; // Añadir servicio de eventos

    public AlbumControllerMvc(AlbumService albumService, EventService eventService) {
        this.albumService = albumService;
        this.eventService = eventService;
    }

    @GetMapping("/by-event/{eventId}")
    public String viewAlbumsByEvent(@PathVariable Long eventId, Model model) {
        var albums = albumService.findByEventId(eventId);
        var evento = eventService.findById(eventId).orElse(null); // Obtener el evento
        model.addAttribute("albumes", albums);
        model.addAttribute("evento", evento);
        return "albumes-by-event";
    }
}
