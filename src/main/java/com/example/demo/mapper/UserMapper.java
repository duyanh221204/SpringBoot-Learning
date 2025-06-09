package com.example.demo.mapper;

import com.example.demo.dto.request.UserCreateRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper
{
    UserEntity toEntity(UserCreateRequest request);

    UserResponse toResponse(UserEntity userEntity);

    void update(@MappingTarget UserEntity user, UserUpdateRequest request);
}
