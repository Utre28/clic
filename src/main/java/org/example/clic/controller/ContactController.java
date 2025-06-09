package org.example.clic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/api/contact")
    public String enviarContacto(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String mensaje,
            Model model
    ) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo("wallapopalejandro50@gmail.com");
            mail.setSubject("Nuevo mensaje de contacto de " + nombre);
            mail.setText("Nombre: " + nombre + "\nEmail: " + email + "\n\nMensaje:\n" + mensaje);
            mailSender.send(mail);
            model.addAttribute("contactSuccess", true);
        } catch (Exception ex) {
            ex.printStackTrace(); // Esto mostrará el error real en consola
            model.addAttribute("contactError", true);
        }
        return "contacto";
    }
}
