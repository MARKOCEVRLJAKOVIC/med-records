package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.User;
import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findAllByUser(User user);

    Optional<Client> findByIdAndUser(Long id, User user);

}
