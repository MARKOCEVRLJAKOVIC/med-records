package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.PhotoDto;
import dev.marko.MedRecords.dtos.UploadPhotoRequest;
import dev.marko.MedRecords.entities.Photo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PhotoMapper {

    PhotoDto toDto(Photo photo);
    Photo toEntity(UploadPhotoRequest request);

    List<PhotoDto> toListDto(List<Photo> photoList);

}
