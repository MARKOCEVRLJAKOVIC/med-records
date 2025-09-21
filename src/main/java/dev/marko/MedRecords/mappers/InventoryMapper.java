package dev.marko.MedRecords.mappers;

import dev.marko.MedRecords.dtos.CreateInventoryRequest;
import dev.marko.MedRecords.dtos.InventoryDto;
import dev.marko.MedRecords.dtos.UpdateInventoryRequest;
import dev.marko.MedRecords.entities.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "providerId", source = "provider.id")
    InventoryDto toDto(Inventory inventory);
    Inventory toEntity(CreateInventoryRequest request);

    List<InventoryDto> toListDto(List<Inventory> inventoryList);

    void update(UpdateInventoryRequest request, @MappingTarget Inventory inventory);

}
