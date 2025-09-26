package dev.marko.MedRecords.services;

import com.twilio.rest.api.v2010.account.Message;

import com.twilio.type.PhoneNumber;
import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.config.TwilioConfig;
import dev.marko.MedRecords.dtos.SendSmsRequest;
import dev.marko.MedRecords.dtos.SmsMessageDto;
import dev.marko.MedRecords.entities.SmsMessage;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.SmsMessageNotFoundException;
import dev.marko.MedRecords.mappers.SmsMessageMapper;
import dev.marko.MedRecords.repositories.ProviderRepository;
import dev.marko.MedRecords.repositories.SmsMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class SmsService {

    private final TwilioConfig twilioConfig;
    private final AuthService authService;
    private final ProviderRepository providerRepository;
    private final SmsMessageRepository smsMessageRepository;
    private final SmsMessageMapper smsMessageMapper;

    public SmsMessageDto getSmsMessage(Long id) {

        var user = authService.getCurrentUser();
        var smsMessage = getSmsMessageForRole(id, user);

        return smsMessageMapper.toDto(smsMessage);

    }

    @Transactional
    public SmsMessageDto sendSmsMessage(SendSmsRequest request) {

        Message.creator(
                new PhoneNumber(request.getToNumber()),
                new PhoneNumber(twilioConfig.getPhoneNumber()),
                request.getBody()
        ).create();

        return null;

    }

    private void sendSms(SendSmsRequest request) {
        Message.creator(
                new PhoneNumber(request.getToNumber()),
                new PhoneNumber(twilioConfig.getPhoneNumber()),
                request.getBody()
        ).create();
    }

    private SmsMessage getSmsMessageForRole(Long smsId, User user) {
        return switch (user.getRole()) {
            case ADMIN -> smsMessageRepository.findById(smsId)
                    .orElseThrow(SmsMessageNotFoundException::new);
            case PROVIDER -> smsMessageRepository.findByIdAndProviderUser(smsId, user)
                    .orElseThrow(SmsMessageNotFoundException::new);
            default -> throw new AccessDeniedException("Access denied");
        };
    }

}
