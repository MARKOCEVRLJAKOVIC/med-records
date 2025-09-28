package dev.marko.MedRecords.exceptions;

public class MedicalRecordNotFoundException extends RuntimeException{

    public MedicalRecordNotFoundException() {
        super("Medical record not found");
    }


    public MedicalRecordNotFoundException(Long id) {
        super("Medical record with id " + id + " not found");
    }

}
