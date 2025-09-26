package dev.marko.MedRecords.services;

import com.twilio.rest.api.v2010.account.Message;

import com.twilio.type.PhoneNumber;
import dev.marko.MedRecords.config.TwilioConfig;
import dev.marko.MedRecords.dtos.SendSmsRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SmsService {

    private final TwilioConfig twilioConfig;


    public void sendSms(SendSmsRequest request) {
        Message.creator(
                new PhoneNumber(request.getTo()),
                new PhoneNumber(twilioConfig.getPhoneNumber()),
                request.getMessage()
        ).create();
    }

}
