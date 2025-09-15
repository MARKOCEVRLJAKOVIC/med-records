package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.PhotoDto;
import dev.marko.MedRecords.dtos.UploadPhotoRequest;
import dev.marko.MedRecords.services.PhotoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping("/upload")
    public ResponseEntity<PhotoDto> upload(@RequestPart("file") MultipartFile file,
                                           @RequestPart("data") @Valid UploadPhotoRequest request,
                                           UriComponentsBuilder builder) throws IOException {

        var photoDto = photoService.uploadPhoto(file, request);
        var uri = builder.path("/photos/{id}").buildAndExpand(photoDto.getId()).toUri();

        return ResponseEntity.created(uri).body(photoDto);

    }

}

