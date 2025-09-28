package dev.marko.MedRecords.exceptions;

public class RoomNotFoundException extends RuntimeException{

    public RoomNotFoundException(){
        super("Room not found");
    }

    public RoomNotFoundException(Long id){
        super("Room with id " + id + " not found");
    }
}
