package dev.marko.MedRecords.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@AllArgsConstructor
@Service
public class CloudinaryPhotoStorageService implements PhotoStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadPhoto(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "image",
                "type", "authenticated"
        ));
        return uploadResult.get("public_id").toString();
    }

    @Override
    public String generateSignedUrl(String publicId) {
        Map options = ObjectUtils.asMap(
                "resource_type", "image",
                "type", "authenticated",
                "expires_at", System.currentTimeMillis() / 1000 + 60 * 10
        );
        return cloudinary.url().signed(true).generate(publicId);
    }

    @Override
    public void deletePhoto(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete photo from Cloudinary", e);
        }
    }
}
