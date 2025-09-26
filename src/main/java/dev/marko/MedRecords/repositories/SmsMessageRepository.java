package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.SmsMessage;
import dev.marko.MedRecords.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmsMessageRepository extends JpaRepository<SmsMessage, Long> {

    Optional<SmsMessage> findByIdAndProviderUser(Long id, User user);

}
