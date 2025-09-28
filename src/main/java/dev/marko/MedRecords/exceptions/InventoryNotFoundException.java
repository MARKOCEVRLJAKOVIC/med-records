package dev.marko.MedRecords.exceptions;

public class InventoryNotFoundException extends RuntimeException{

    public InventoryNotFoundException() {
        super("Inventory not found");
    }

    public InventoryNotFoundException(Long id) {
        super("Inventory with id " + id + " not found");
    }

}
