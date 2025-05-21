package org.example.clic.service;

import org.example.clic.model.Album;
import java.util.List;
import java.util.Optional;

public interface AlbumService {
    List<Album> findAll();
    List<Album> findByEventId(Long eventId);
    Optional<Album> findById(Long id);
    Album save(Album album);
    void deleteById(Long id);
    boolean existsById(Long id);
}