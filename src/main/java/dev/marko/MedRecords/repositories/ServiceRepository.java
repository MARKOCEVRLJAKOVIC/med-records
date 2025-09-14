package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.Service;
import dev.marko.MedRecords.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findAllByProvider(Provider provider);

}
