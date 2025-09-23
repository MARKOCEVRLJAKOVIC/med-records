package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.calendar.CalendarSlot;
import dev.marko.MedRecords.calendar.TimeSlotDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CalendarSlotMapper {

    public static TimeSlotDto toDto(CalendarSlot slot) {
        TimeSlotDto dto = new TimeSlotDto();
        dto.setStartTime(slot.getStartTime().toString());
        dto.setEndTime(slot.getEndTime().toString());
        dto.setStatus(slot.getStatus().name());
        dto.setAppointmentId(slot.getAppointment() != null ? slot.getAppointment().getId() : null);
        dto.setClientName(slot.getClientName());
        return dto;
    }

    public static List<TimeSlotDto> toDtoList(List<CalendarSlot> slots) {
        return slots.stream()
                .map(CalendarSlotMapper::toDto)
                .toList();
    }
}
