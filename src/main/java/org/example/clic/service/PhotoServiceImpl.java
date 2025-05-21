package org.example.clic.service;

import org.example.clic.model.Photo;
import org.example.clic.repository.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PhotoServiceImpl implements PhotoService {
    private final PhotoRepository photoRepository;

    public PhotoServiceImpl(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @Override
    public List<Photo> findAll() {
        return photoRepository.findAll();
    }
    @Override
    public List<Photo> findByAlbumId(Long albumId) {
        return photoRepository.findByAlbumId(albumId);
    }

    @Override
    public Optional<Photo> findById(Long id) {
        return photoRepository.findById(id);
    }

    @Override
    public Photo save(Photo photo) {
        return photoRepository.save(photo);
    }

    @Override
    public void deleteById(Long id) {
        photoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return photoRepository.existsById(id);
    }
}