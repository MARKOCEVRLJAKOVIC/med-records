package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.IncomingMessagesRequest;
import dev.marko.MedRecords.dtos.ProviderPhoneNumberDto;
import dev.marko.MedRecords.dtos.SendSmsRequest;
import dev.marko.MedRecords.dtos.SmsMessageDto;
import dev.marko.MedRecords.services.SmsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@AllArgsConstructor
@RestController
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    @GetMapping("/{id}")
    public ResponseEntity<SmsMessageDto> getSmsMessage(@PathVariable Long id){

        var smsMessageDto = smsService.getSmsMessage(id);
        return ResponseEntity.ok(smsMessageDto);

    }

    @PostMapping
    public ResponseEntity<SmsMessageDto> sendSmsMessage(@RequestBody SendSmsRequest request,
                                                        UriComponentsBuilder builder) {

        var smsMessageDto = smsService.sendSmsMessage(request);
        var uri = builder.path("/sms/{id}").buildAndExpand(smsMessageDto.getId()).toUri();

        return ResponseEntity.created(uri).body(smsMessageDto);

    }

    @PostMapping("/receive")
    public ResponseEntity<SmsMessageDto> receiveMessage(@RequestBody IncomingMessagesRequest request,
                                               UriComponentsBuilder builder) {

        var smsMessageDto = smsService.handleIncomingMessages(request);
        var uri = builder.path("/sms/{id}").buildAndExpand(smsMessageDto.getId()).toUri();

        return ResponseEntity.created(uri).body(smsMessageDto);

    }

    @PostMapping("/providerId/buy-phone-number")
    public ResponseEntity<ProviderPhoneNumberDto> buyPhoneNumber(@RequestParam Long providerId,
                                                                 @RequestParam String areaCode){

        var phoneNumber = smsService.buyNumberForProvider(providerId, areaCode);
        return ResponseEntity.ok(phoneNumber);

    }

}
