package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.ProviderPhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderPhoneNumberRepository extends JpaRepository<ProviderPhoneNumber, Long> {
}
