package org.example.clic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.example.clic.service.PhotoService;

@Controller
@RequestMapping("/photos")
public class PhotoViewController {

    @Autowired
    private PhotoService photoService;

    @GetMapping("/mis-fotos")
    public String misFotosPage() {
        return "mis-fotos"; // Sirve el HTML desde /templates/mis-fotos.html
    }

    @PostMapping("/borrar-multiple")
    public String borrarMultipleFotos(@RequestParam(value = "fotoIds", required = false) List<Long> fotoIds,
                                      RedirectAttributes redirectAttributes) {
        if (fotoIds == null || fotoIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "No se seleccionaron fotos para borrar.");
            return "redirect:/panel/eventos";
        }
        try {
            for (Long id : fotoIds) {
                photoService.deleteById(id);
            }
            redirectAttributes.addFlashAttribute("mensaje", "Fotos borradas correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Ocurrió un error inesperado al borrar las fotos.");
        }
        return "redirect:/panel/eventos";
    }
}

