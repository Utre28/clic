package org.example.clic.dto;

import java.time.LocalDateTime;

/**
 * DTO para registrar accesos de usuarios a fotos.
 */
public class AccessLogDTO {
    private Long id;// Identificador único del registro de acceso

    private Long userId; // ID del usuario que accede

    private Long photoId; // ID de la foto accedida

    private LocalDateTime accessedAt;  // Fecha y hora del acceso

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public LocalDateTime getAccessedAt() {
        return accessedAt;
    }

    public void setAccessedAt(LocalDateTime accessedAt) {
        this.accessedAt = accessedAt;
    }
}
