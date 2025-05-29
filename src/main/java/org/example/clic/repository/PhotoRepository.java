package org.example.clic.repository;

import org.example.clic.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
// Repositorio JPA para Photo
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    // Encuentra fotos por ID de álbum
    List<Photo> findByAlbumId(Long albumId);
    @Query("SELECT p FROM Photo p " +
            "JOIN p.album a " +
            "JOIN a.event e " +
            "JOIN e.client u " +
            "WHERE u.email = :email")
    List<Photo> findPhotosByUserEmail(@Param("email") String email);
}
