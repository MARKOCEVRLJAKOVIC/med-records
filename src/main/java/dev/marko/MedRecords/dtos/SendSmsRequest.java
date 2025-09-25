package dev.marko.MedRecords.dtos;

import lombok.Data;

@Data
public class SendSmsRequest {

    private String to;
    private String message;

}
