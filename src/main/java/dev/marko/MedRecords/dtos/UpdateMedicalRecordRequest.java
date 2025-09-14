package dev.marko.MedRecords.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UpdateMedicalRecordRequest {

    private Timestamp date;
    private String notes;
    private Long clientId;
    private Long serviceId;
    private Long providerId;

}
