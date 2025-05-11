package org.example.clic.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AccessLogDTO {
    private Long id;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    @NotNull(message = "Photo ID is mandatory")
    private Long photoId;

    @NotNull(message = "Access timestamp is mandatory")
    private LocalDateTime accessedAt;

    // getters & setters...


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
