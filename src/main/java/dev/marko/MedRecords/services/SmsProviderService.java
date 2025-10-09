package dev.marko.MedRecords.services;

import dev.marko.MedRecords.dtos.ProviderPhoneNumberDto;
import dev.marko.MedRecords.dtos.SendSmsRequest;
import dev.marko.MedRecords.entities.ProviderPhoneNumber;

public interface SmsProviderService {

    void sendSmsMessage(SendSmsRequest request, ProviderPhoneNumber providerPhoneNumber);
    ProviderPhoneNumberDto buyNumberForProvider(Long providerId, String areaCode);

}
