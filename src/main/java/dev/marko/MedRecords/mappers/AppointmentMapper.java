package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.AppointmentDto;
import dev.marko.MedRecords.dtos.BookAppointmentRequest;
import dev.marko.MedRecords.dtos.UpdateAppointmentRequest;
import dev.marko.MedRecords.entities.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "providerId", source = "provider.id")
    @Mapping(target = "serviceId", source = "service.id")
    AppointmentDto toDto(Appointment appointment);

    Appointment toEntity(BookAppointmentRequest request);

    List<AppointmentDto> toListDto(List<Appointment> appointmentList);

    void update(UpdateAppointmentRequest request, @MappingTarget Appointment appointment);


}
