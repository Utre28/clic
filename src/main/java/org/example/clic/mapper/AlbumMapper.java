package org.example.clic.mapper;

import org.example.clic.dto.AlbumDTO;
import org.example.clic.model.Album;
import org.example.clic.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    @Mapping(source = "event.id", target = "eventId")
    AlbumDTO toDto(Album album);

    @Mapping(target = "event", expression = "java(createEvent(dto.getEventId()))")
    Album toEntity(AlbumDTO dto);

    default Event createEvent(Long id) {
        if (id == null) return null;
        Event e = new Event();
        e.setId(id);
        return e;
    }
}