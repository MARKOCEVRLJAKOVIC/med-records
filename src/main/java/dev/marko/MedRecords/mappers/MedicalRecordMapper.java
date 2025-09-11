package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.CreateMedicalRecordRequest;
import dev.marko.MedRecords.dtos.MedicalRecordDto;
import dev.marko.MedRecords.dtos.UpdateMedicalRecordRequest;
import dev.marko.MedRecords.entities.MedicalRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicalRecordMapper {

    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "providerId", source = "provider.id")
    @Mapping(target = "clientId", source = "client.id")
    MedicalRecordDto toDto(MedicalRecord medicalRecord);
    MedicalRecord toEntity(CreateMedicalRecordRequest request);

    List<MedicalRecordDto> toListDto(List<MedicalRecord> medicalRecordList);

    void update(UpdateMedicalRecordRequest request, @MappingTarget MedicalRecord medicalRecord);

}
