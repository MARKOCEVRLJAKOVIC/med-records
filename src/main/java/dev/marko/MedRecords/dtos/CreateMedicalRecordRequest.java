package dev.marko.MedRecords.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.Service;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CreateMedicalRecordRequest {

    private Timestamp date;
    private String notes;
    private Long clientId;
    private Long serviceId;
    private Long providerId;

}
