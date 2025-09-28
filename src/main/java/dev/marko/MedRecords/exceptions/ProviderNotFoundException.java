package dev.marko.MedRecords.exceptions;

public class ProviderNotFoundException extends RuntimeException {

    public ProviderNotFoundException() {
        super("Provider not found");
    }

    public ProviderNotFoundException(Long id) {
        super("Provider with id " + id + " not found");
    }
}
