package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.PhotoDto;
import dev.marko.MedRecords.dtos.UploadPhotoRequest;
import dev.marko.MedRecords.entities.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PhotoMapper {

    @Mapping(target = "url", ignore = true)
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "medicalRecordId", source = "medicalRecord.id")
    PhotoDto toDto(Photo photo);
    Photo toEntity(UploadPhotoRequest request);

    List<PhotoDto> toListDto(List<Photo> photoList);

}
