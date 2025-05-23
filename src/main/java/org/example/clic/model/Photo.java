package org.example.clic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity // Mapea la clase a la tabla 'photos'
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Clave primaria

    private String url; // URL de la foto

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt; // Fecha de subida

    private String description; // Descripción opcional

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album; // Álbum asociado


    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccessLog> accessLogs; // Historial de accesos

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

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<AccessLog> getAccessLogs() {
        return accessLogs;
    }

    public void setAccessLogs(List<AccessLog> accessLogs) {
        this.accessLogs = accessLogs;
    }

}


