package dev.marko.MedRecords.calendar;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CalendarDayDto {

    private LocalDate date;
    private boolean available;
    private List<TimeSlotDto> timeSlots;

}