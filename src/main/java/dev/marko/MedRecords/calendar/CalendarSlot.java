package dev.marko.MedRecords.calendar;

import dev.marko.MedRecords.entities.Appointment;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.SlotStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CalendarSlot {

    private Long id;
    private Provider provider;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private SlotStatus status;
    private Appointment appointment;
    private String clientName;


}