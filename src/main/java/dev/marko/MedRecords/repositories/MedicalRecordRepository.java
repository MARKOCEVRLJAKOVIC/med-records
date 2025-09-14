package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.MedicalRecord;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findAllByProviderId(Long id);
    List<MedicalRecord> findAllByProvider(Provider provider);
    List<MedicalRecord> findAllByProviderAndUser(Provider provider, User user);
    List<MedicalRecord> findAllByClient(Client client);

    Optional<MedicalRecord> findByIdAndClientUser(Long id, User user);
    Optional<MedicalRecord> findByIdAndProviderUser(Long id, User user);


}
