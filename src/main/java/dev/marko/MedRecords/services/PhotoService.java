package dev.marko.MedRecords.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class PhotoService {

    private final Cloudinary cloudinary;

    public PhotoService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadPhoto(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString(); // this is the url that will be stored in db
    }
}
