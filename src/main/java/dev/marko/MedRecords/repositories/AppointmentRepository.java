package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Appointment;
import dev.marko.MedRecords.entities.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {


    List<Appointment> findAllByProviderId(Long id);

    List<Appointment> findAllByProviderIdAndClientId(Long providerId, Long clientId);



}
