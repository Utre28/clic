package org.example.clic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class EventDTO {
    private Long id;

    @NotBlank(message = "Event name is mandatory")
    private String name;

    @NotNull(message = "Date is mandatory")
    private LocalDate date;

    @NotBlank(message = "Location is mandatory")
    private String location;

    @NotNull(message = "Client ID is mandatory")
    private Long clientId;

    // getters & setters...

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
}
