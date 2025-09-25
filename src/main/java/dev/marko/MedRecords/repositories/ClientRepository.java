package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.AppointmentStatus;
import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {


    @Query("""
       SELECT DISTINCT c
       FROM Client c
       JOIN c.appointments a
       WHERE a.provider.id = :providerId
       """)
    List<Client> findAllByProviderViaAppointments(@Param("providerId") Long providerId);

    @Query("""
       SELECT DISTINCT c
       FROM Client c
       JOIN c.appointments a
       WHERE a.provider = :provider
         AND c.user = :user
       """)
    List<Client> findAllByProviderViaAppointmentsAndUser(@Param("provider") Provider provider,
                                                         @Param("user") User user);

    @Query("""
       SELECT DISTINCT c
       FROM Client c
       JOIN c.appointments a
       WHERE a.provider = :provider
         AND c.user = :user
         AND c.id = :clientId
       """)
    Optional<Client> findByIdAndProviderViaAppointmentsAndUser(@Param("clientId") Long clientId,
                                                               @Param("provider") Provider provider,
                                                               @Param("user") User user);

    @Query("""
       SELECT DISTINCT c
       FROM Client c
       JOIN c.appointments a
       WHERE c.id = :id
         AND a.provider.user = :user
       """)
    Optional<Client> findByIdAndProviderUser(@Param("id") Long id,
                                             @Param("user") User user);

    Optional<Client> findByIdAndUser(Long id, User user);


    @Query("""
       SELECT DISTINCT c
       FROM Client c
       JOIN c.appointments a
       WHERE a.provider = :provider AND
       a.status = :status
       """)
    List<Client> findAllByProviderViaAppointmentStatus(@Param("provider") Provider provider,
                                                       @Param("status") AppointmentStatus status);



}
