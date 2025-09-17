package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByUser(User user);
    Optional<Provider> findByIdAndUser(Long id, User user);

}
