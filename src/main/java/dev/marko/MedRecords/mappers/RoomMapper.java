package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.RegisterRoomRequest;
import dev.marko.MedRecords.dtos.RoomDto;
import dev.marko.MedRecords.dtos.UpdateRoomRequest;
import dev.marko.MedRecords.entities.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "providerId", source = "provider.id")
    RoomDto toDto(Room room);
    Room toEntity(RegisterRoomRequest request);

    List<RoomDto> toListDto(List<Room> rooms);

    void update(UpdateRoomRequest request, @MappingTarget Room room);

}
