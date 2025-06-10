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
        var eventoOpt = eventService.findById(eventId); // Obtener el evento
        if (eventoOpt.isPresent()) {
            var evento = eventoOpt.get();
            // Incrementar visitas
            evento.setVisits(evento.getVisits() + 1);
            eventService.save(evento);
            model.addAttribute("evento", evento);
        } else {
            model.addAttribute("evento", null);
        }
        model.addAttribute("albumes", albums);
        return "albumes-by-event";
    }
}
