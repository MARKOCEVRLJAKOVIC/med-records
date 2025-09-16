package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.PhotoType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UpdatePhotoRequest {

    private PhotoType type;
    private Timestamp takenAt;
    private Long clientId;
    private Long medicalRecordId;

}
