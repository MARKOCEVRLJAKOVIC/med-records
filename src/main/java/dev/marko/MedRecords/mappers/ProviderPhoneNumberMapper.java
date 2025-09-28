package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.ProviderPhoneNumberDto;
import dev.marko.MedRecords.entities.ProviderPhoneNumber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProviderPhoneNumberMapper {

    @Mapping(target = "providerId", source = "provider.id")
    ProviderPhoneNumberDto toDto(ProviderPhoneNumber providerPhoneNumber);


}
