package dev.marko.MedRecords.calendar;

import dev.marko.MedRecords.entities.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProviderCalendarDto {

    private Long providerId;
    private String providerName;
    private String specialty;

    private List<CalendarDayDto> daysDto;


}
