package dev.marko.MedRecords.dtos;

import lombok.Data;

@Data
public class RoomDto {

    private Long id;
    private String name;
    private String location;
    private String type;
    private Integer capacity;
    private Boolean isActive;
    private Long providerId;

}
