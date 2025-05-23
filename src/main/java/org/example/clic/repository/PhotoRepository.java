package org.example.clic.repository;

import org.example.clic.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
// Repositorio JPA para Photo
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    // Encuentra fotos por ID de álbum
    List<Photo> findByAlbumId(Long albumId);
}
