package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.RegisterRoomRequest;
import dev.marko.MedRecords.dtos.RoomDto;
import dev.marko.MedRecords.entities.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "providerId", source = "provider.id")
    RoomDto toDto(Room room);

    Room toEntity(RegisterRoomRequest request);

}
