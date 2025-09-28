package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.ProviderPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderPhoneNumberRepository extends JpaRepository<ProviderPhoneNumber, Long> {

    Optional<ProviderPhoneNumber> findByProvider(Provider provider);
    Optional<ProviderPhoneNumber> findByPhoneNumber(String phoneNumber);

}
