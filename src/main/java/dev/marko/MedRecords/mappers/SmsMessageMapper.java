package dev.marko.MedRecords.mappers;

import com.twilio.twiml.voice.Sms;
import dev.marko.MedRecords.dtos.SendSmsRequest;
import dev.marko.MedRecords.dtos.SmsMessageDto;
import dev.marko.MedRecords.entities.SmsMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SmsMessageMapper {

    @Mapping(target = "providerId", source = "provider.id")
    SmsMessageDto toDto(SmsMessage smsMessage);
    SmsMessage toEntity(SendSmsRequest request);

}
