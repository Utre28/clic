package org.example.clic.controller;

import org.example.clic.service.AlbumService;
import org.example.clic.service.PhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AlbumViewController {
    private final AlbumService albumService;
    private final PhotoService photoService;

    public AlbumViewController(AlbumService albumService, PhotoService photoService) {
        this.albumService = albumService;
        this.photoService = photoService;
    }

    @GetMapping("/album/{id}")
    public String verAlbum(@PathVariable Long id, Model model,
                           @RequestParam(value = "mis", required = false, defaultValue = "false") boolean desdeMisEventos) {
        var albumOpt = albumService.findById(id);
        if (albumOpt.isEmpty()) {
            model.addAttribute("message", "Álbum no encontrado.");
            return "error";
        }
        var album = albumOpt.get();
        var fotos = photoService.findByAlbumId(id);
        model.addAttribute("album", album);
        model.addAttribute("fotos", fotos);
        model.addAttribute("eventId", album.getEvent().getId()); // Añade el eventId
        model.addAttribute("desdeMisEventos", desdeMisEventos); // Añade el flag
        return "album";
    }
}