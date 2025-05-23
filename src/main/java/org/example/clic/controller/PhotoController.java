package org.example.clic.controller;

import jakarta.validation.Valid;
import org.example.clic.dto.PhotoDTO;
import org.example.clic.mapper.PhotoMapper;
import org.example.clic.repository.AlbumRepository;
import org.example.clic.repository.PhotoRepository;
import org.example.clic.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;
import org.example.clic.model.Album;
import org.example.clic.model.Photo;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    private final PhotoService photoService; // Servicio de fotos
    private final PhotoMapper photoMapper;// Mapper entre entidad y DTO

    // Inyección por constructor de servicios y mappers
    public PhotoController(PhotoService photoService, PhotoMapper photoMapper) {
        this.photoService = photoService;
        this.photoMapper = photoMapper;
    }
    @Autowired // Inyecta repositorios si los necesitas directamente
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    /**
     * Obtiene todas las fotos.
     */
    @GetMapping
    public List<PhotoDTO> getAll() {
        return photoService.findAll().stream()
                .map(photoMapper::toDto)
                .collect(Collectors.toList());
    }
    /**
     * Obtiene las fotos de un álbum específico.
     */
    @GetMapping("/by-album/{albumId}")
    public List<PhotoDTO> getPhotosByAlbum(@PathVariable Long albumId) {
        return photoService.findByAlbumId(albumId).stream()
                .map(photoMapper::toDto)
                .collect(Collectors.toList());
    }
    /**
     * Obtiene una foto por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PhotoDTO> getById(@PathVariable Long id) {
        return photoService.findById(id)
                .map(photoMapper::toDto)// Convierte entidad a DTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea una foto a partir de un DTO.
     * Valida y devuelve 201 Created con ubicación.
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PhotoDTO dto, BindingResult br) {
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        // Guarda usando el servicio y mapea a DTO
        var saved = photoService.save(photoMapper.toEntity(dto));
        var out = photoMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/photos/" + out.getId())).body(out);
    }
    /**
     * Actualiza una foto existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody PhotoDTO dto,
                                    BindingResult br) {
        if (br.hasErrors()) {
            var errors = br.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        return photoService.findById(id)
                .map(existing -> {
                    // Mapea DTO a entidad, conserva el ID
                    var toUpdate = photoMapper.toEntity(dto);
                    toUpdate.setId(id);
                    var updated = photoService.save(toUpdate);
                    return ResponseEntity.ok(photoMapper.toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Elimina una foto por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (photoService.existsById(id)) {
            photoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    /**
     * Sube una foto a un directorio y guarda metadatos en BD.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("albumId") Long albumId,
            @RequestParam(value = "description", required = false) String description
    ) {
        try {
            // Crear carpeta si no existe
            String uploadDir = "uploads/album_" + albumId;
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // Nombre único del archivo
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Crear y guardar entidad Photo
            Photo photo = new Photo();
            Album album = new Album();
            album.setId(albumId);
            photo.setAlbum(album);
            // URL relativa para servir estáticos
            photo.setUrl("/" + filePath.toString().replace("\\", "/"));
            photo.setUploadedAt(LocalDateTime.now());
            photo.setDescription(description);

            Photo saved = photoService.save(photo);
            return ResponseEntity.ok(photoMapper.toDto(saved));

        } catch (IOException e) {
            // Error de I/O, devuelve 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar la imagen.");
        }
    }
    /**
     * Sube múltiples fotos y guarda en BD.
     */
    @PostMapping("/upload-multiple")
    public ResponseEntity<?> uploadMultiplePhotos(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("albumId") Long albumId) {
        // Verifica existencia de álbum
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Álbum no encontrado"));

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path uploadPath = Paths.get("uploads");
                    Files.createDirectories(uploadPath); // Crear carpeta si no existe
                    Path filePath = uploadPath.resolve(filename);
                    Files.write(filePath, file.getBytes());

                    // Guarda entidad Photo con URL y álbum asociado
                    Photo photo = new Photo();
                    photo.setUrl("/uploads/" + filename);
                    photo.setAlbum(album);
                    photoRepository.save(photo);
                } catch (IOException e) {
                    // Si falla una, responde error
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al guardar: " + file.getOriginalFilename());
                }
            }
        }

        // Si todas subieron bien
        return ResponseEntity.ok("Fotos subidas correctamente");
    }


}