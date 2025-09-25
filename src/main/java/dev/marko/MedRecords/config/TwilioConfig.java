package dev.marko.MedRecords.config;

import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Getter
    @Value("${twilio.phone-number}")
    private String phoneNumber;

    public TwilioRestClient twilioRestClient() {
        Twilio.init(accountSid, authToken);
        return Twilio.getRestClient();
    }

}
