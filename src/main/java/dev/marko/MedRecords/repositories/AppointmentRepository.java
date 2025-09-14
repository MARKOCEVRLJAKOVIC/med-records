package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Appointment;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.User;
import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByProviderId(Long id);
    List<Appointment> findAllByClientId(Long id);
    Optional<List<Appointment>> findAllByClientIdAndProviderUser(Long id, User user);
    Optional<List<Appointment>> findAllByClientIdAndClientUser(Long id, User user);

    Optional<Appointment> findByIdAndClientUser(Long id, User user);
    Optional<Appointment> findByIdAndProviderUser(Long id, User user);

}
