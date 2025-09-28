package dev.marko.MedRecords.exceptions;

public class SmsMessageNotFoundException extends RuntimeException{

    public SmsMessageNotFoundException(){
        super("Sms message not found");
    }

    public SmsMessageNotFoundException(Long id){
        super("Sms message with id " + id + " not found");
    }
}
