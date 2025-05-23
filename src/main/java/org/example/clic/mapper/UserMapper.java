package org.example.clic.mapper;

import org.example.clic.dto.UserDTO;
import org.example.clic.model.User;
import org.mapstruct.Mapper;

// Mapper para convertir entre User y UserDTO
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);// Convierte entidad User a UserDTO
    User toEntity(UserDTO dto);// Convierte UserDTO a entidad User
}