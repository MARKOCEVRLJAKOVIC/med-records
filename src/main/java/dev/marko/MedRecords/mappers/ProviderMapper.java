package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.ProviderDto;
import dev.marko.MedRecords.dtos.UpdateClientRequest;
import dev.marko.MedRecords.dtos.UpdateProviderRequest;
import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.Provider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProviderMapper {

    @Mapping(target = "userId", source = "user.id")
    ProviderDto toDto(Provider provider);
    Provider toEntity(ProviderDto providerDto);

    List<ProviderDto> toListDto(List<Provider> providerList);

    default void update(UpdateProviderRequest r, Provider p) {
        if (r.getFirstName() != null) p.setFirstName(r.getFirstName());
        if (r.getLastName() != null) p.setLastName(r.getLastName());
        if (r.getPhone() != null) p.setPhone(r.getPhone());
        if (r.getSpecialty() != null) p.setSpecialty(r.getSpecialty());
        if (r.getLicenseNumber() != null) p.setLicenseNumber(r.getLicenseNumber());
        if (r.getEmploymentStart() != null) p.setEmploymentStart(r.getEmploymentStart());
        if (r.getEmploymentEnd() != null) p.setEmploymentEnd(r.getEmploymentEnd());
    }
}
