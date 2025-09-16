package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.PhotoDto;
import dev.marko.MedRecords.dtos.UpdatePhotoRequest;
import dev.marko.MedRecords.entities.PhotoType;
import dev.marko.MedRecords.services.PhotoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping("/{clientId}/client")
    public ResponseEntity<List<PhotoDto>> findAllPhotosForClient(@PathVariable Long clientId){

        var photoListDto = photoService.findAllPhotosForClient(clientId);
        return ResponseEntity.ok(photoListDto);

    }

    @GetMapping("/{id}")
    public ResponseEntity<PhotoDto> findPhoto(@PathVariable Long id){

        var photoDto = photoService.findPhoto(id);
        return ResponseEntity.ok(photoDto);

    }


    @PostMapping("/upload")
    public ResponseEntity<PhotoDto> upload(@RequestParam("file") MultipartFile file,
                                           @RequestParam("type") PhotoType type,
                                           @RequestParam("takenAt") Timestamp takenAt,
                                           @RequestParam("clientId") Long clientId,
                                           @RequestParam("medicalRecordId") Long medicalRecordId,
                                           UriComponentsBuilder builder) throws IOException {


        var photoDto = photoService.uploadPhoto(file, type,takenAt, clientId, medicalRecordId);
        var uri = builder.path("/photos/{id}").buildAndExpand(photoDto.getId()).toUri();

        return ResponseEntity.created(uri).body(photoDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<PhotoDto> updatePhoto(@PathVariable Long id,
                                                @RequestBody @Valid UpdatePhotoRequest request){

        var photoDto = photoService.updatePhoto(id, request);
        return ResponseEntity.ok(photoDto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id){

        photoService.deletePhoto(id);
        return ResponseEntity.noContent().build();

    }

}

