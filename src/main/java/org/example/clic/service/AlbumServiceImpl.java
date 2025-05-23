package org.example.clic.service;

import org.example.clic.model.Album;
import org.example.clic.repository.AlbumRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional // Gestiona transacciones automáticamente
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;

    public AlbumServiceImpl(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public List<Album> findAll() {
        return albumRepository.findAll(); // Devuelve todos los álbumes
    }

    @Override
    public Optional<Album> findById(Long id) {
        return albumRepository.findById(id); // Busca álbum por ID
    }

    @Override
    public Album save(Album album) {
        return albumRepository.save(album); // Inserta o actualiza un álbum
    }

    @Override
    public void deleteById(Long id) {
        albumRepository.deleteById(id);  // Elimina un álbum
    }

    @Override
    public boolean existsById(Long id) {
        return albumRepository.existsById(id); // Verifica existencia
    }

    @Override
    public List<Album> findByEventId(Long eventId) {
        return albumRepository.findByEventId(eventId); // Obtiene álbumes de un evento
    }

}
