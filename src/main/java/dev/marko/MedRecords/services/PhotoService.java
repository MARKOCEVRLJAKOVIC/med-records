package dev.marko.MedRecords.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.PhotoDto;
import dev.marko.MedRecords.dtos.UploadPhotoRequest;
import dev.marko.MedRecords.entities.Photo;
import dev.marko.MedRecords.entities.PhotoType;
import dev.marko.MedRecords.entities.Role;
import dev.marko.MedRecords.exceptions.ClientNotFoundException;
import dev.marko.MedRecords.exceptions.MedicalRecordNotFoundException;
import dev.marko.MedRecords.exceptions.PhotoNotFoundException;
import dev.marko.MedRecords.mappers.PhotoMapper;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.MedicalRecordRepository;
import dev.marko.MedRecords.repositories.PhotoRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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

    public PhotoDto findPhoto(Long id){

        var user = authService.getCurrentUser();


        var photo = switch (user.getRole()){
            case ADMIN, PROVIDER -> photoRepository.findById(id)
                    .orElseThrow(PhotoNotFoundException::new);
            case CLIENT -> photoRepository.findByIdAndUser(id, user)
                    .orElseThrow(PhotoNotFoundException::new);
            default -> throw new AccessDeniedException("");
        };

        if(user.getRole() == Role.PROVIDER &&
                !photo.getMedicalRecord().getProvider().equals(user.getProvider())){
            throw new AccessDeniedException("You can only view photos of your clients");
        }

        var photoDto = photoMapper.toDto(photo);
        photoDto.setUrl(getSignedPhotoUrl(photo.getPublicId()));

        return photoDto;

    }

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

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "image",
                "type", "authenticated"
        ));

        String publicId = uploadResult.get("public_id").toString();

        var photo = Photo.builder()
                .publicId(publicId)
                .takenAt(takenAt)
                .client(client)
                .medicalRecord(medicalRecord)
                .type(type)
                .build();

        photoRepository.save(photo);
        return photoMapper.toDto(photo);

    }



    // methods

    private String getSignedPhotoUrl(String publicId) {
        Map options = ObjectUtils.asMap(
                "resource_type", "image",
                "type", "authenticated",
                "expires_at", System.currentTimeMillis() / 1000 + 60 * 10 // 10 minuta
        );

        return cloudinary.url().signed(true).generate(publicId);
    }

}
