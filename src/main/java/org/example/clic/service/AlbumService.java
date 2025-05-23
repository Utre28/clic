package org.example.clic.service;

import org.example.clic.model.Album;
import java.util.List;
import java.util.Optional;
// Servicio para gestionar operaciones de álbumes
public interface AlbumService {
    List<Album> findAll(); // Obtiene todos los álbumes
    List<Album> findByEventId(Long eventId);  // Obtiene álbumes de un evento específico
    Optional<Album> findById(Long id);  // Busca un álbum por su ID
    Album save(Album album);   // Crea o actualiza un álbum
    void deleteById(Long id);  // Elimina un álbum por ID
    boolean existsById(Long id); // Verifica si un álbum existe
}