package dev.marko.MedRecords.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.PhotoDto;
import dev.marko.MedRecords.dtos.UploadPhotoRequest;
import dev.marko.MedRecords.exceptions.ClientNotFoundException;
import dev.marko.MedRecords.exceptions.MedicalRecordNotFoundException;
import dev.marko.MedRecords.mappers.PhotoMapper;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.MedicalRecordRepository;
import dev.marko.MedRecords.repositories.PhotoRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
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

    public PhotoDto uploadPhoto(MultipartFile file,
                                UploadPhotoRequest request) throws IOException {

        var user = authService.getCurrentUser();

        var client = clientRepository.findByIdAndUser(request.getClientId(), user)
                .orElseThrow(ClientNotFoundException::new);

        var medicalRecord = medicalRecordRepository.findByIdAndClientUser(request.getMedicalRecordId(), user)
                .orElseThrow(MedicalRecordNotFoundException::new);

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String url = uploadResult.get("secure_url").toString(); // this is the url that will be stored in db

        var photo = photoMapper.toEntity(request);
        photo.setUrl(url);
        photo.setClient(client);
        photo.setMedicalRecord(medicalRecord);

        photoRepository.save(photo);
        return photoMapper.toDto(photo);

    }
}
