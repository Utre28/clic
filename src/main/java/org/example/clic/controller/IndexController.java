package org.example.clic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/eventos")
    public String eventos() {
        return "eventos";  // Debe corresponder con eventos.html en templates
    }
}
