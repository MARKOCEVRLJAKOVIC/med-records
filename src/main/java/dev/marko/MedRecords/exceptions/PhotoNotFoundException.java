package dev.marko.MedRecords.exceptions;

public class PhotoNotFoundException extends RuntimeException{

    public PhotoNotFoundException(){
        super("Photo not found");
    }

    public PhotoNotFoundException(Long id){
        super("Photo with id " + id + " not found");
    }
}
