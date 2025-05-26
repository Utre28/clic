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
        return "categoria-bodas";
    }

    @GetMapping("/portafolio/bautizos")
    public String bautizos() {
        return "categoria-bautizos";
    }

    @GetMapping("/portafolio/comuniones")
    public String comuniones() {
        return "categoria-comuniones";
    }

    @GetMapping("/portafolio/retratos")
    public String retratos() {
        return "categoria-retratos";
    }

    @GetMapping("/portafolio/conciertos")
    public String conciertos() {
        return "categoria-conciertos";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }
}
