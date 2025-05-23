package org.example.clic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PanelController {

    @GetMapping("/panel")
    public String panel() {
        // Thymeleaf buscará src/main/resources/templates/panel.html
        return "panel";
    }
    @GetMapping("/subir-fotos.html")
    public String subirFotos() {
        // devolverá src/main/resources/templates/subir-fotos.html
        return "subir-fotos";
    }
}