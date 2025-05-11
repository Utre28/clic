package org.example.clic.mapper;

import org.example.clic.dto.PhotoDTO;
import org.example.clic.model.Photo;
import org.example.clic.model.Album;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PhotoMapper {
    @Mapping(source = "album.id", target = "albumId")
    PhotoDTO toDto(Photo photo);

    @Mapping(target = "album", expression = "java(createAlbum(dto.getAlbumId()))")
    Photo toEntity(PhotoDTO dto);

    default Album createAlbum(Long id) {
        if (id == null) return null;
        Album a = new Album();
        a.setId(id);
        return a;
    }
}