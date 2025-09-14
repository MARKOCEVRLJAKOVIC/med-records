package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.CreateServiceRequest;
import dev.marko.MedRecords.dtos.ServiceDto;
import dev.marko.MedRecords.dtos.UpdateServiceRequest;
import dev.marko.MedRecords.entities.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.lang.annotation.Target;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "providerId", source = "provider.id")
    ServiceDto toDto(Service service);
    Service toEntity(CreateServiceRequest request);

    List<ServiceDto> toListDto(List<Service> serviceList);

    void update(UpdateServiceRequest request, @MappingTarget Service service);

}
