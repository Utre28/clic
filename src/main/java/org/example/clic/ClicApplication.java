package org.example.clic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication // Marca esta clase como punto de entrada de Spring Boot
public class ClicApplication {

    public static void main(String[] args) {
        // Antes de SpringApplication.run:
        new File("uploads").mkdirs();
        // Inicia la aplicación Spring Boot
        SpringApplication.run(ClicApplication.class, args);
    }
}
