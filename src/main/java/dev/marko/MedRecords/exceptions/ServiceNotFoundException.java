package dev.marko.MedRecords.exceptions;

public class ServiceNotFoundException extends RuntimeException{

    public ServiceNotFoundException(){
        super("Service with id not found");
    }

    public ServiceNotFoundException(Long id){
        super("Service with id " + id + " not found");
    }
}
