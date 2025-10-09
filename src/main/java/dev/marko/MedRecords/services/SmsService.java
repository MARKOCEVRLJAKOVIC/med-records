package dev.marko.MedRecords.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.IncomingMessagesRequest;
import dev.marko.MedRecords.dtos.SendSmsRequest;
import dev.marko.MedRecords.dtos.SmsMessageDto;
import dev.marko.MedRecords.entities.*;
import dev.marko.MedRecords.exceptions.PhoneNumberNotFoundException;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.exceptions.SmsMessageNotFoundException;
import dev.marko.MedRecords.mappers.SmsMessageMapper;
import dev.marko.MedRecords.repositories.ProviderPhoneNumberRepository;
import dev.marko.MedRecords.repositories.ProviderRepository;
import dev.marko.MedRecords.repositories.SmsMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class SmsService {

    private final AuthService authService;
    private final ProviderRepository providerRepository;
    private final SmsMessageRepository smsMessageRepository;
    private final SmsMessageMapper smsMessageMapper;
    private final ProviderPhoneNumberRepository providerPhoneNumberRepository;

    public SmsMessageDto getSmsMessage(Long id) {

        var user = authService.getCurrentUser();
        var smsMessage = getSmsMessageForRole(id, user);

        return smsMessageMapper.toDto(smsMessage);

    }

    @Transactional
    public SmsMessageDto sendSmsMessage(SendSmsRequest request) {

        var user = authService.getCurrentUser();

        var provider = providerRepository.findByIdAndUser(request.getProviderId(), user)
                .orElseThrow(ProviderNotFoundException::new);

        var providerPhoneNumber = providerPhoneNumberRepository.findByProvider(provider)
                .orElseThrow(PhoneNumberNotFoundException::new);

        SmsStatus status;

        try {

            sendSmsMessage(request, providerPhoneNumber);
            status = SmsStatus.SENT;

        } catch (Exception e) {

            status = SmsStatus.FAILED;

        }


        var smsMessage = SmsMessage.builder()
                .fromNumber(providerPhoneNumber.getPhoneNumber())
                .toNumber(request.getToNumber())
                .body(request.getBody())
                .direction(Direction.SENT)
                .status(status)
                .provider(provider)
                .build();

        smsMessageRepository.save(smsMessage);

        return smsMessageMapper.toDto(smsMessage);

    }

    public SmsMessageDto handleIncomingMessages(IncomingMessagesRequest request){

        var providerPhoneNumber = providerPhoneNumberRepository.findByPhoneNumber(request.getFrom())
                .orElseThrow(PhoneNumberNotFoundException::new);

        var smsMessage = SmsMessage.builder()
                .fromNumber(request.getFrom())
                .toNumber(request.getTo())
                .body(request.getBody())
                .direction(Direction.RECEIVED)
                .status(SmsStatus.SENT)
                .twilioSid(request.getSmsSid())
                .provider(providerPhoneNumber.getProvider())
                .build();

        if (!smsMessageRepository.existsByTwilioSid(request.getSmsSid())) {
            smsMessageRepository.save(smsMessage);
        }

        return smsMessageMapper.toDto(smsMessage);

    }

    private static void sendSmsMessage(SendSmsRequest request, ProviderPhoneNumber providerPhoneNumber) {
        Message.creator(
                new PhoneNumber(request.getToNumber()),
                new PhoneNumber(providerPhoneNumber.getPhoneNumber()),
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
