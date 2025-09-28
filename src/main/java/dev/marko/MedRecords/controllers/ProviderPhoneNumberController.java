package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.ProviderPhoneNumberDto;
import dev.marko.MedRecords.services.TwilioPhoneNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/providers/phone-numbers")
@RequiredArgsConstructor
public class ProviderPhoneNumberController {

    private final TwilioPhoneNumberService twilioPhoneNumberService;

    public ResponseEntity<ProviderPhoneNumberDto> buyPhoneNumber(@RequestParam Long providerId,
                                                                 @RequestParam String areaCode){

        var phoneNumber = twilioPhoneNumberService.buyNumberForProvider(providerId, areaCode);
        return ResponseEntity.ok(phoneNumber);

    }

}
