package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.AppointmentDto;
import dev.marko.MedRecords.dtos.ProviderDto;
import dev.marko.MedRecords.entities.Appointment;
import dev.marko.MedRecords.entities.Provider;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    List<AppointmentDto> toListDto(List<Appointment> appointmentList);


}
