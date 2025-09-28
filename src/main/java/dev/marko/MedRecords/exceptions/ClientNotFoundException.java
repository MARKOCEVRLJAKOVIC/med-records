package dev.marko.MedRecords.exceptions;

public class ClientNotFoundException extends RuntimeException{

    public ClientNotFoundException(){
        super("Client not found");
    }


    public ClientNotFoundException(Long id){
        super("Client with id " + id + " not found");
    }

}
