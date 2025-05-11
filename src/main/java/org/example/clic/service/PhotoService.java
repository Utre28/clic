package org.example.clic.service;

import org.example.clic.model.Photo;
import java.util.List;
import java.util.Optional;

public interface PhotoService {
    List<Photo> findAll();
    Optional<Photo> findById(Long id);
    Photo save(Photo photo);
    void deleteById(Long id);
    boolean existsById(Long id);
}