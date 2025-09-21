package dev.marko.MedRecords.calendar;

import dev.marko.MedRecords.entities.AppointmentStatus;
import lombok.Data;

@Data
public class TimeSlotDto {

    private String startTime;
    private String endTime;
    private String status;
    private Long appointmentId; // if booked
    private String clientName; // if booked

}