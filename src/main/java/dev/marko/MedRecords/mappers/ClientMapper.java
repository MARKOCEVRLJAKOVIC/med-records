package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.auth.RegisterClientRequest;
import dev.marko.MedRecords.dtos.ClientDto;
import dev.marko.MedRecords.dtos.ProviderDto;
import dev.marko.MedRecords.dtos.UpdateClientRequest;
import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    default void update(UpdateClientRequest r, Client c) {
        if (r.getFirstName() != null) c.setFirstName(r.getFirstName());
        if (r.getLastName() != null) c.setLastName(r.getLastName());
        if (r.getPhone() != null) c.setPhone(r.getPhone());
        if (r.getDateOfBirth() != null) c.setDateOfBirth(r.getDateOfBirth());
        if (r.getGender() != null) c.setGender(r.getGender());
        if (r.getAddress() != null) c.setAddress(r.getAddress());
        if (r.getAllergies() != null) c.setAllergies(r.getAllergies());
        if (r.getMedicalNotes() != null) c.setMedicalNotes(r.getMedicalNotes());
    }

    @Mapping(target = "userId", source = "user.id")
    ClientDto toDto(Client client);

    List<ClientDto> toListDto(List<Client> clientList);

}
