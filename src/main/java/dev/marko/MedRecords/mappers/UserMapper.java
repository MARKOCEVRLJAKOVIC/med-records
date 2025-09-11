package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.auth.RegisterClientRequest;
import dev.marko.MedRecords.dtos.RegisterAdminRequest;
import dev.marko.MedRecords.dtos.UpdateClientRequest;
import dev.marko.MedRecords.dtos.UpdateProviderRequest;
import dev.marko.MedRecords.dtos.UserDto;
import dev.marko.MedRecords.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(RegisterAdminRequest request);
    UserDto toDto(User user);

    void updateClient(UpdateClientRequest request, @MappingTarget User user);
    void updateFromProviderRequest(UpdateProviderRequest request, @MappingTarget User user);

}

