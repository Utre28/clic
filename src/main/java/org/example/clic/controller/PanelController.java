// src/main/java/org/example/clic/controller/PanelController.java
package org.example.clic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PanelController {

    @GetMapping("/panel")
    public String panel() {
        return "panel";
    }

    @GetMapping("/subir-fotos")
    public String subirFotos() {
        return "subir-fotos";
    }
}
