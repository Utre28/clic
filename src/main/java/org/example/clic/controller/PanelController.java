// src/main/java/org/example/clic/controller/PanelController.java
package org.example.clic.controller;

import org.example.clic.model.Event;
import org.example.clic.service.EventService;
import org.example.clic.service.AlbumService;
import org.example.clic.service.PhotoService;
import org.example.clic.model.Photo;
import org.example.clic.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PanelController {

    private static final Logger log = LoggerFactory.getLogger(PanelController.class);

    private final EventService eventService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private EmailService emailService;

    public PanelController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/panel")
    public String panel() {
        return "panel";
    }

    @GetMapping("/subir-fotos")
    public String subirFotos() {
        return "subir-fotos";
    }

    @GetMapping("/panel/eventos")
    public String panelEventos(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String eventoCategoria,
            Model model,
            Authentication authentication
    ) {
        log.info("Entrando en /panel/eventos con q={}, eventoCategoria={}, usuario={}", q, eventoCategoria, authentication != null ? authentication.getName() : "null");

        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PHOTOGRAPHER"))) {
            model.addAttribute("message", "Acceso solo para fotógrafos.");
            return "error";
        }

        List<Event> allEvents = eventService.findAll();
        log.info("Eventos recuperados: {}", allEvents != null ? allEvents.size() : "null");

        if (allEvents == null) allEvents = List.of();

        List<Event> topEvents = allEvents.stream().limit(5).toList();

        List<Event> filteredEvents = allEvents;
        if (eventoCategoria != null && !eventoCategoria.isBlank()) {
            filteredEvents = filteredEvents.stream()
                .filter(e -> e.getCategory() != null && e.getCategory().equalsIgnoreCase(eventoCategoria))
                .toList();
        }
        if (q != null && !q.isBlank()) {
            filteredEvents = filteredEvents.stream()
                .filter(e -> e.getName() != null && e.getName().toLowerCase().contains(q.toLowerCase()))
                .toList();
        }

        log.info("Eventos filtrados: {}", filteredEvents.size());

        model.addAttribute("topEvents", topEvents);
        model.addAttribute("filteredEvents", filteredEvents);
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("eventoCategoria", eventoCategoria != null ? eventoCategoria : "");

        return "panel-eventos";
    }

    @GetMapping("/panel/eventos/{eventoId}")
    public String verAlbumesEvento(@PathVariable Long eventoId, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PHOTOGRAPHER"))) {
            model.addAttribute("message", "Acceso solo para fotógrafos.");
            return "error";
        }
        var evento = eventService.findById(eventoId).orElse(null);
        if (evento == null) {
            model.addAttribute("message", "Evento no encontrado");
            return "error";
        }
        var albumes = albumService.findByEventId(eventoId);

        // Cargar las fotos de cada álbum y pasarlas como un Map
        Map<Long, List<Photo>> albumFotosMap = new HashMap<>();
        for (var album : albumes) {
            albumFotosMap.put(album.getId(), photoService.findByAlbumId(album.getId()));
        }

        model.addAttribute("evento", evento);
        model.addAttribute("albumes", albumes);
        model.addAttribute("albumFotosMap", albumFotosMap);
        return "panel-albumes";
    }

    // Borrar evento
    @PostMapping("/panel/eventos/{eventoId}/borrar")
    public String borrarEvento(@PathVariable Long eventoId, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PHOTOGRAPHER"))) {
            model.addAttribute("message", "Acceso solo para fotógrafos.");
            return "error";
        }
        eventService.deleteById(eventoId);
        return "redirect:/panel/eventos";
    }

    // Borrar álbum
    @PostMapping("/panel/albumes/{albumId}/borrar")
    public String borrarAlbum(@PathVariable Long albumId, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PHOTOGRAPHER"))) {
            model.addAttribute("message", "Acceso solo para fotógrafos.");
            return "error";
        }
        albumService.deleteById(albumId);
        // Redirige a la página anterior o al listado de eventos
        return "redirect:/panel/eventos";
    }

    // Borrar foto
    @PostMapping("/panel/fotos/{fotoId}/borrar")
    public String borrarFoto(@PathVariable Long fotoId, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PHOTOGRAPHER"))) {
            model.addAttribute("message", "Acceso solo para fotógrafos.");
            return "error";
        }
        photoService.deleteById(fotoId);
        // Redirige a la página anterior o al listado de eventos
        return "redirect:/panel/eventos";
    }

    // Borrado múltiple de eventos
    @PostMapping("/panel/eventos/borrar-multiple")
    public String borrarEventosMultiple(@RequestParam(value = "eventoIds", required = false) List<Long> eventoIds,
                                        Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PHOTOGRAPHER"))) {
            model.addAttribute("message", "Acceso solo para fotógrafos.");
            return "error";
        }
        if (eventoIds != null) {
            for (Long id : eventoIds) {
                eventService.deleteById(id);
            }
        }
        return "redirect:/panel/eventos";
    }

    // Borrado múltiple de álbumes
    @PostMapping("/panel/albumes/borrar-multiple")
    public String borrarAlbumesMultiple(@RequestParam(value = "albumIds", required = false) List<Long> albumIds,
                                        Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PHOTOGRAPHER"))) {
            model.addAttribute("message", "Acceso solo para fotógrafos.");
            return "error";
        }
        if (albumIds != null) {
            for (Long id : albumIds) {
                albumService.deleteById(id);
            }
        }
        return "redirect:/panel/eventos";
    }

    // Borrado múltiple de fotos
    @PostMapping("/panel/fotos/borrar-multiple")
    public String borrarFotosMultiple(@RequestParam(value = "fotoIds", required = false) List<Long> fotoIds,
                                      Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PHOTOGRAPHER"))) {
            model.addAttribute("message", "Acceso solo para fotógrafos.");
            return "error";
        }
        if (fotoIds != null) {
            for (Long id : fotoIds) {
                photoService.deleteById(id);
            }
        }
        return "redirect:/panel/eventos";
    }

    // Enviar mensaje al cliente del evento
    @PostMapping("/panel/eventos/{eventoId}/enviar-mensaje")
    public String enviarMensajeCliente(@PathVariable Long eventoId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PHOTOGRAPHER"))) {
            return "redirect:/panel/eventos?mensaje=Acceso%20solo%20para%20fot%C3%B3grafos&mensajeId=" + eventoId;
        }
        var eventoOpt = eventService.findById(eventoId);
        if (eventoOpt.isEmpty()) {
            return "redirect:/panel/eventos?mensaje=Evento%20no%20encontrado&mensajeId=" + eventoId;
        }
        var evento = eventoOpt.get();
        if (evento.getClient() == null || evento.getClient().getEmail() == null) {
            return "redirect:/panel/eventos?mensaje=El%20evento%20no%20tiene%20cliente%20asociado&mensajeId=" + eventoId;
        }
        try {
            String to = evento.getClient().getEmail();
            String subject = "¡Tus fotos ya están disponibles!";
            String body = "Hola " + evento.getClient().getName() + ",\n\nTus fotos del evento '" + evento.getName() + "' ya están disponibles.\n\nPuedes acceder a ellas desde la web.\n\nUn saludo,\nClic y Color";
            emailService.sendEmail(to, subject, body);
            return "redirect:/panel/eventos?mensaje=Mensaje%20enviado%20correctamente&mensajeId=" + eventoId;
        } catch (Exception e) {
            return "redirect:/panel/eventos?mensaje=Ha%20ocurrido%20un%20error%20al%20enviar%20el%20mensaje&mensajeId=" + eventoId;
        }
    }
}
