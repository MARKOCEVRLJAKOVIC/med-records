package dev.marko.MedRecords.services;

import com.twilio.rest.api.v2010.account.IncomingPhoneNumber;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.availablephonenumbercountry.Local;
import com.twilio.type.PhoneNumber;
import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.ProviderPhoneNumberDto;
import dev.marko.MedRecords.dtos.SendSmsRequest;
import dev.marko.MedRecords.entities.ProviderPhoneNumber;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.mappers.ProviderMapper;
import dev.marko.MedRecords.mappers.ProviderPhoneNumberMapper;
import dev.marko.MedRecords.repositories.ProviderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class TwilioSmsProvider implements SmsProviderService{

    private final AuthService authService;
    private final ProviderRepository providerRepository;
    private final ProviderPhoneNumberMapper providerPhoneNumberMapper;

    @Override
    public void sendSmsMessage(SendSmsRequest request, ProviderPhoneNumber providerPhoneNumber) {
        Message.creator(
                new PhoneNumber(request.getToNumber()),
                new PhoneNumber(providerPhoneNumber.getPhoneNumber()),
                request.getBody()
        ).create();
    }

    @Override
    public ProviderPhoneNumberDto buyNumberForProvider(Long providerId, String areaCode) {

        var user = authService.getCurrentUser();

        var provider = switch (user.getRole()) {
            case ADMIN -> providerRepository.findById(providerId)
                    .orElseThrow(ProviderNotFoundException::new);
            case PROVIDER -> providerRepository.findByIdAndUser(providerId, user)
                    .orElseThrow(ProviderNotFoundException::new);
            default -> throw new AccessDeniedException("Access denied");
        };

        var availableNumbers = Local.reader("US")
                .setAreaCode(Integer.parseInt(areaCode))
                .limit(1)
                .read();

        if (!availableNumbers.iterator().hasNext()) {
            throw new RuntimeException("No numbers available for this area code");
        }

        var number = availableNumbers.iterator().next();

        IncomingPhoneNumber purchasedNumber = IncomingPhoneNumber.creator(
                new PhoneNumber(number.getPhoneNumber().toString())
        ).create();

        ProviderPhoneNumber providerPhoneNumber = ProviderPhoneNumber.builder()
                .phoneNumber(purchasedNumber.getPhoneNumber().toString())
                .twilioSid(purchasedNumber.getSid())
                .provider(provider)
                .build();

        return providerPhoneNumberMapper.toDto(providerPhoneNumber);

    }
}
