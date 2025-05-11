package org.example.clic.mapper;

import org.example.clic.dto.AccessLogDTO;
import org.example.clic.model.AccessLog;
import org.example.clic.model.Photo;
import org.example.clic.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccessLogMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "photo.id", target = "photoId")
    AccessLogDTO toDto(AccessLog log);

    @Mapping(target = "user", expression = "java(createUser(dto.getUserId()))")
    @Mapping(target = "photo", expression = "java(createPhoto(dto.getPhotoId()))")
    AccessLog toEntity(AccessLogDTO dto);

    default User createUser(Long id) {
        if (id == null) return null;
        User u = new User();
        u.setId(id);
        return u;
    }

    default Photo createPhoto(Long id) {
        if (id == null) return null;
        Photo p = new Photo();
        p.setId(id);
        return p;
    }
}