package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.Direction;
import dev.marko.MedRecords.entities.SmsStatus;
import lombok.Data;

@Data
public class SendSmsRequest {

    private Long id;
    private String toNumber;
    private String body;
    private Long providerId;

}
