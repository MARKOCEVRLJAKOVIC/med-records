package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.CreateInventoryRequest;
import dev.marko.MedRecords.dtos.InventoryDto;
import dev.marko.MedRecords.dtos.UpdateInventoryRequest;
import dev.marko.MedRecords.services.InventoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    public final InventoryService inventoryService;

    @GetMapping("/providerId")
    public ResponseEntity<List<InventoryDto>> findAllInventoriesForProvider(@PathVariable Long providerId){

        var inventoryDtoList = inventoryService.findAllInventoriesForProvider(providerId);
        return ResponseEntity.ok(inventoryDtoList);

    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDto> findInventory(@PathVariable Long id){

        var inventoryDto = inventoryService.findInventory(id);
        return ResponseEntity.ok(inventoryDto);

    }

    @PostMapping
    public ResponseEntity<InventoryDto> createInventory(@RequestBody @Valid CreateInventoryRequest request,
                                                        UriComponentsBuilder builder) {

        var inventoryDto = inventoryService.createInventory(request);
        var uri = builder.path("/inventory/{id}").buildAndExpand(inventoryDto.getId()).toUri();

        return ResponseEntity.created(uri).body(inventoryDto);


    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryDto> updateInventory(@PathVariable Long id,@RequestBody UpdateInventoryRequest request){

        var inventoryDto = inventoryService.updateInventory(id, request);
        return ResponseEntity.ok(inventoryDto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InventoryDto> deleteInventory(@PathVariable Long id){

        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();

    }

}
