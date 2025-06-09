package org.example.clic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoriaController {
    @GetMapping("/categoria-bodas")
    public String categoriaBodas() {
        return "categoria-bodas";
    }

    @GetMapping("/categoria-bautizos")
    public String categoriaBautizos() {
        return "categoria-bautizos";
    }

    @GetMapping("/categoria-comuniones")
    public String categoriaComuniones() {
        return "categoria-comuniones";
    }

    @GetMapping("/categoria-retratos")
    public String categoriaRetratos() {
        return "categoria-retratos";
    }

    @GetMapping("/categoria-conciertos")
    public String categoriaConciertos() {
        return "categoria-conciertos";
    }
}
