package org.example.clic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity // Marca la clase como entidad JPA
@Table(name = "access_log")// Mapea a la tabla "access_log"
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Clave primaria autogenerada

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;  // Usuario que realiza el acceso

    @ManyToOne(optional = false)
    @JoinColumn(name = "photo_id")
    private Photo photo;  // Foto accedida

    @Column(name = "accessed_at", nullable = false)
    private LocalDateTime accessedAt; // Fecha y hora del acceso

    // getters y setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public LocalDateTime getAccessedAt() {
        return accessedAt;
    }

    public void setAccessedAt(LocalDateTime accessedAt) {
        this.accessedAt = accessedAt;
    }
}
