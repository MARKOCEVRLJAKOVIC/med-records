package dev.marko.MedRecords.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IncomingMessagesRequest {

    @JsonProperty("From")
    private String from;

    @JsonProperty("To")
    private String to;

    @JsonProperty("Body")
    private String body;

    @JsonProperty("SmsSid")
    private String smsSid;

}
