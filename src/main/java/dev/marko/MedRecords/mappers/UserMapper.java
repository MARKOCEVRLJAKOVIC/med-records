package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.auth.RegisterClientRequest;
import dev.marko.MedRecords.dtos.*;
import dev.marko.MedRecords.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(RegisterAdminRequest request);
    UserDto toDto(User user);

    List<UserDto> toListDto(List<User> userList);

    void update(UpdateUserRequest request, @MappingTarget User user);
    void updateFromProviderRequest(UpdateProviderRequest request, @MappingTarget User user);

}

