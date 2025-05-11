package org.example.clic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AlbumDTO {
    private Long id;

    @NotBlank(message = "Album name is mandatory")
    private String name;

    @NotNull(message = "Event ID is mandatory")
    private Long eventId;

    @NotNull(message = "Creation timestamp is mandatory")
    private LocalDateTime createdAt;

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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
