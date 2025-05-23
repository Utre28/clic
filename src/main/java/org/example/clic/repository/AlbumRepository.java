package org.example.clic.repository;

import org.example.clic.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
// Repositorio JPA para Album
public interface AlbumRepository extends JpaRepository<Album, Long> {

    // Método para obtener álbumes por ID de evento
    List<Album> findByEventId(Long eventId);
}
