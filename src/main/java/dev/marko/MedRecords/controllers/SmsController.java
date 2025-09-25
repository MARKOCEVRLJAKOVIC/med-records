package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.GenericResponse;
import dev.marko.MedRecords.dtos.SendSmsRequest;
import dev.marko.MedRecords.services.SmsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<String> sendSms(@RequestBody SendSmsRequest request){

        smsService.sendSms(request);
        return ResponseEntity.ok("Message sent!");

    }

    @PostMapping("/receive")
    public String receiveSms(@RequestParam("From") String from,
                             @RequestParam("Body") String body) {
        System.out.println("Message from: " + from + " Message: " + body);
        return "<Response><Message>Thanks!</Message></Response>";
    }

}
