package org.example.clic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/photos")
public class PhotoViewController {

    @GetMapping("/mis-fotos")
    public String misFotosPage() {
        return "mis-fotos"; // Sirve el HTML desde /templates/mis-fotos.html
    }
}

