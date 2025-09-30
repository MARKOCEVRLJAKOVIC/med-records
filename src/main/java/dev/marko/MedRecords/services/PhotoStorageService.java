package dev.marko.MedRecords.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoStorageService {

    String uploadPhoto(MultipartFile file) throws IOException;

    String generateSignedUrl(String publicId);
    void deletePhoto(String publicId);

}
