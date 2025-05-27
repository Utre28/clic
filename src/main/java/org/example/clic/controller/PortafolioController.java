package org.example.clic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PortafolioController {

    @GetMapping("/portafolio")
    public String portafolioCategorias() {
        return "portafolio-categorias"; // tu HTML de categorías
    }

    @GetMapping("/portafolio/bodas")
    public String bodas() {
        return "portafolio/bodas";
    }

    @GetMapping("/portafolio/bautizos")
    public String bautizos() {
        return "portafolio/bautizos";
    }

    @GetMapping("/portafolio/comuniones")
    public String comuniones() {
        return "portafolio/comuniones";
    }

    @GetMapping("/portafolio/retratos")
    public String retratos() {
        return "portafolio/retratos";
    }

    @GetMapping("/portafolio/conciertos")
    public String conciertos() {
        return "portafolio/conciertos";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }
}
