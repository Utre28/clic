package org.example.clic.mapper;

import org.example.clic.dto.EventDTO;
import org.example.clic.model.Event;
import org.example.clic.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "client.id", target = "clientId")
    EventDTO toDto(Event event);

    @Mapping(target = "client", expression = "java(createUser(dto.getClientId()))")
    Event toEntity(EventDTO dto);

    default User createUser(Long id) {
        if (id == null) return null;
        User u = new User();
        u.setId(id);
        return u;
    }
}