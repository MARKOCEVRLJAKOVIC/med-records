package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.AppointmentStatus;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class BookAppointmentRequest {

    private Timestamp startTime;
    private Timestamp endTime;
    private AppointmentStatus status;
    private String notes;
    private Long clientId;
    private Long providerId;
    private List<Long> serviceIds;

}
