package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.AppointmentStatus;
import dev.marko.MedRecords.entities.Provider;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;

@Data
public class AppointmentDto {

    private Long id;
    private Timestamp startTime;
    private Timestamp endTime;
    private AppointmentStatus status;
    private String notes;
    private Long providerId;
    private Long serviceId;



}
