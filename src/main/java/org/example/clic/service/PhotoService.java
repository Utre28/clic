package org.example.clic.service;

import org.example.clic.dto.PhotoDTO;
import org.example.clic.model.Photo;
import java.util.List;
import java.util.Optional;

// Servicio para gestionar operaciones de Photo
public interface PhotoService {
    List<Photo> findAll();  // Obtiene todas las fotos
    List<Photo> findByAlbumId(Long albumId); // Obtiene fotos de un álbum específico
    Optional<Photo> findById(Long id); // Busca foto por ID
    Photo save(Photo photo);// Crea o actualiza una foto
    void deleteById(Long id);// Elimina una foto por ID
    boolean existsById(Long id);// Verifica si una foto existe
    List<Photo> findAllById(String[] ids); // Obtiene fotos por una lista de IDs

    List<PhotoDTO> findPhotosByUserEmail(String email);
}