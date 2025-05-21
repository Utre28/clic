package org.example.clic.repository;

import org.example.clic.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByEventId(Long eventId);
}
