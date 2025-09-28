package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.Direction;
import dev.marko.MedRecords.entities.SmsStatus;
import lombok.Data;

@Data
public class SmsMessageDto {

    private Long id;
    private String toNumber;
    private String fromNumber;
    private String body;
    private Direction direction;
    private SmsStatus status;
    private Long providerId;

}
