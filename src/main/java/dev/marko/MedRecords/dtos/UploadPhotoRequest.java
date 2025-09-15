package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.PhotoType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@Data
public class UploadPhotoRequest {

    private PhotoType type;
    private Timestamp takenAt;
    private Long clientId;
    private Long medicalRecordId;


}
