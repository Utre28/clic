package org.example.clic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "file:uploads/" debe ser la ruta absoluta o relativa correcta donde están los archivos
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}

