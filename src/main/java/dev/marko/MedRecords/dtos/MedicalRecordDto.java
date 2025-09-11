package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.Service;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class MedicalRecordDto {

    private Long id;
    private Timestamp date;
    private String notes;
    private Long clientId;
    private Long serviceId;
    private Long providerId;

}
