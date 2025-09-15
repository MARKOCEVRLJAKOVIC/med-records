package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.PhotoType;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PhotoDto {

    private Long id;
    private String url;
    private PhotoType type;
    private Timestamp takenAt;
    private Long clientId;
    private Long medicalRecordId;

}
