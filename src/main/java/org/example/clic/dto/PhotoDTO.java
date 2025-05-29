package org.example.clic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

// DTO para transferir datos de Foto
public class PhotoDTO {
    private Long id;

    @NotBlank(message ="La URL es obligatoria")
    private String url; // Ruta de la foto

    @NotNull(message =  "La fecha de subida es obligatoria")
    private LocalDateTime uploadedAt; // Fecha de subida

    private String description;

    @NotNull(message = "El ID del álbum es obligatorio")
    private Long albumId;  // Álbum asociado

    public PhotoDTO(Long id, String url, String description, LocalDateTime uploadedAt, Long albumId) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.uploadedAt = uploadedAt;
        this.albumId = albumId;
    }

    // getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }
}
