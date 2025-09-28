package dev.marko.MedRecords.exceptions;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException() {
        super("Incorrect email or password");
    }
}
