package dev.marko.MedRecords.exceptions;

public class PhoneNumberNotFoundException extends RuntimeException {

    public PhoneNumberNotFoundException() {
        super("Phone number not found");
    }


    public PhoneNumberNotFoundException(Long id) {
        super("Phone number with id " + id + " not found");
    }

}
