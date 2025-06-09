package org.example.clic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
// DTO para transferencia de datos de Evento
public class EventDTO {
    private Long id;

    @NotBlank(message = "El nombre del evento es obligatorio")
    private String name;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @NotBlank(message = "La ubicación es obligatoria")
    private String location;
    private Long clientId;// ID del cliente relacionado (opcional)
    private String category;// Categoría del evento (opcional)
    private boolean privado = true;

    // getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPrivado() { return privado; }
    public void setPrivado(boolean privado) { this.privado = privado; }
}
