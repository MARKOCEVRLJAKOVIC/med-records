package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.CreateInventoryRequest;
import dev.marko.MedRecords.dtos.InventoryDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @PostMapping
    public ResponseEntity<InventoryDto> createInventory(@RequestBody @Valid CreateInventoryRequest request) {

        return null;

    }


}
