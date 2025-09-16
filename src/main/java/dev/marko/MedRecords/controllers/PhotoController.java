package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.PhotoDto;
import dev.marko.MedRecords.entities.PhotoType;
import dev.marko.MedRecords.services.PhotoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.sql.Timestamp;

@AllArgsConstructor
@RestController
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping("/{id}")
    public ResponseEntity<PhotoDto> findPhoto(@PathVariable Long id){

        return null;

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

}

