package org.example.clic.mapper;

import org.example.clic.dto.UserDTO;
import org.example.clic.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO dto);
}