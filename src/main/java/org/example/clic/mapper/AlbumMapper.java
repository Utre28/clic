package org.example.clic.mapper;

import org.example.clic.dto.AlbumDTO;
import org.example.clic.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface AlbumMapper {
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(target = "mainPhotoUrl", expression = "java(getMainPhotoUrl(album))")
    AlbumDTO toDto(Album album);
    default String getMainPhotoUrl(Album album) {
        if (album.getPhotos() != null && !album.getPhotos().isEmpty()) {
            return album.getPhotos().get(0).getUrl(); // La primera foto como imagen principal
        }
        return null; // O ruta a una imagen placeholder
    }

    // Ignoramos la creación del evento dentro de MapStruct, lo gestionaremos en AlbumMapperImpl
    @Mapping(target = "event", ignore = true)
    Album toEntity(AlbumDTO dto);
}
