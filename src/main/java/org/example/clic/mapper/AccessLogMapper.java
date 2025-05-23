package org.example.clic.mapper;

import org.example.clic.dto.AccessLogDTO;
import org.example.clic.model.AccessLog;
import org.example.clic.model.Photo;
import org.example.clic.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
// Mapper para convertir entre AccessLog y AccessLogDTO
@Mapper(componentModel = "spring")
public interface AccessLogMapper {

    // Mapea campos de la entidad a los del DTO
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "photo.id", target = "photoId")
    AccessLogDTO toDto(AccessLog log);

    // Mapea campos del DTO a la entidad, creando objetos User y Photo con solo el ID
    @Mapping(target = "user", expression = "java(createUser(dto.getUserId()))")
    @Mapping(target = "photo", expression = "java(createPhoto(dto.getPhotoId()))")
    AccessLog toEntity(AccessLogDTO dto);

    // Crea un User con solo el ID para evitar cargar toda la entidad
    default User createUser(Long id) {
        if (id == null) return null;
        User u = new User();
        u.setId(id);
        return u;
    }
    // Crea un Photo con solo el ID para relacionarlo en la entidad
    default Photo createPhoto(Long id) {
        if (id == null) return null;
        Photo p = new Photo();
        p.setId(id);
        return p;
    }
}