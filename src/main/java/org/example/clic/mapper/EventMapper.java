package org.example.clic.mapper;

import org.example.clic.dto.EventDTO;
import org.example.clic.model.Event;
import org.example.clic.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
// Mapper para convertir entre Event y EventDTO
@Mapper(componentModel = "spring")
public interface EventMapper {

    // Mapea client.id a clientId en el DTO
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "privado", target = "privado")
    EventDTO toDto(Event event);

    // Crea el objeto User con solo el ID al mapear de DTO a entidad
    @Mapping(target = "client", expression = "java(createUser(dto.getClientId()))")
    @Mapping(source = "privado", target = "privado")
    @Mapping(target = "albums", ignore = true)
    Event toEntity(EventDTO dto);

    // Método auxiliar para construir User con ID
    default User createUser(Long id) {
        if (id == null) return null;
        User u = new User();
        u.setId(id);
        return u;
    }
}