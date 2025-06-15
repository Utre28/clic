package org.example.clic.mapper;

import org.example.clic.dto.AlbumDTO;
import org.example.clic.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    // MapStruct puede mapear automáticamente los campos con el mismo nombre
    // Solo necesitas mapear los campos que cambian de nombre o requieren lógica especial
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "mainPhotoUrl", ignore = true)
    AlbumDTO toDto(Album album);

    @Mapping(target = "event", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "privado", ignore = true)
    @Mapping(target = "downloads", ignore = true)
    @Mapping(target = "coverUrl", ignore = true)
    Album toEntity(AlbumDTO dto);
}
