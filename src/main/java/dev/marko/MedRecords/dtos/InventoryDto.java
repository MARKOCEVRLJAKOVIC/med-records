package dev.marko.MedRecords.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InventoryDto {

    private Long id;
    private String productName;
    private String productType;
    private String lotNumber;
    private LocalDate expiryDate;
    private Integer quantity;
    private Integer lowStockThreshold;

}
