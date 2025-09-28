package dev.marko.MedRecords.exceptions;

public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException() {
        super("Appointment not found");
    }

    public AppointmentNotFoundException(Long id) {
        super("Appointment with id " + id + " not found");
    }

}
