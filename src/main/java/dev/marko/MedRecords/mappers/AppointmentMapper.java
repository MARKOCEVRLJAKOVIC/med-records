package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.AppointmentDto;
import dev.marko.MedRecords.dtos.BookAppointmentRequest;
import dev.marko.MedRecords.dtos.UpdateAppointmentRequest;
import dev.marko.MedRecords.entities.Appointment;
import dev.marko.MedRecords.entities.AppointmentService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "providerId", source = "provider.id")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "serviceIds", source = "services", qualifiedByName = "mapAppointmentServicesToServiceIds")
    AppointmentDto toDto(Appointment appointment);

    Appointment toEntity(BookAppointmentRequest request);

    List<AppointmentDto> toListDto(List<Appointment> appointmentList);

    void update(UpdateAppointmentRequest request, @MappingTarget Appointment appointment);

    @Named("mapAppointmentServicesToServiceIds")
    static List<Long> mapAppointmentServicesToServiceIds(List<AppointmentService> appointmentServices) {
        if (appointmentServices == null) return null;
        return appointmentServices.stream()
                .map(appService -> appService.getService().getId())
                .toList();
    }

}
