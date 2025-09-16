package dev.marko.MedRecords.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.PhotoDto;
import dev.marko.MedRecords.dtos.UploadPhotoRequest;
import dev.marko.MedRecords.entities.Photo;
import dev.marko.MedRecords.entities.PhotoType;
import dev.marko.MedRecords.exceptions.ClientNotFoundException;
import dev.marko.MedRecords.exceptions.MedicalRecordNotFoundException;
import dev.marko.MedRecords.mappers.PhotoMapper;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.MedicalRecordRepository;
import dev.marko.MedRecords.repositories.PhotoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@Service
public class PhotoService {

    private final Cloudinary cloudinary;
    private final AuthService authService;
    private final PhotoMapper photoMapper;
    private final PhotoRepository photoRepository;
    private final ClientRepository clientRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Transactional
    public PhotoDto uploadPhoto(MultipartFile file,
                                PhotoType type,
                                Timestamp takenAt,
                                Long clientId,
                                Long medicalRecordId) throws IOException {


        var user = authService.getCurrentUser();

        var client = clientRepository.findByIdAndUser(clientId, user)
                .orElseThrow(ClientNotFoundException::new);

        var medicalRecord = medicalRecordRepository.findByIdAndClient(medicalRecordId, client)
                .orElseThrow(MedicalRecordNotFoundException::new);

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String url = uploadResult.get("secure_url").toString(); // this is the url that will be stored in db


        var photo = Photo.builder()
                .url(url)
                .takenAt(takenAt)
                .client(client)
                .medicalRecord(medicalRecord)
                .type(type)
                .build();

        photoRepository.save(photo);
        return photoMapper.toDto(photo);

    }
}
