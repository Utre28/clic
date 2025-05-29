package org.example.clic.service;

import org.example.clic.dto.PhotoDTO;
import org.example.clic.model.Photo;
import org.example.clic.repository.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@Transactional
public class PhotoServiceImpl implements PhotoService {
    private final PhotoRepository photoRepository; // Repositorio para acceso a datos de Photo

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
        photo.setUploadedAt(LocalDateTime.now());
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

    @Override
    public List<PhotoDTO> findPhotosByUserEmail(String email) {
        List<Photo> photos = photoRepository.findPhotosByUserEmail(email);

        return photos.stream()
                .map(photo -> new PhotoDTO(
                        photo.getId(),
                        photo.getUrl(),
                        photo.getDescription(),
                        photo.getUploadedAt(),
                        photo.getAlbum().getId()
                ))
                .collect(Collectors.toList());
    }
}
