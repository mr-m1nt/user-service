package org.example.mapper;

import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity -> Response
    UserResponse toResponse(User user);

    // Request -> Entity (при создании)
    User toEntity(UserRequest request);

    // Обновление существующей Entity из Request
    void updateEntityFromRequest(UserRequest request, @MappingTarget User user);
}