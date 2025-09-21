package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Appointment;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.User;
import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByProviderId(Long id);
    List<Appointment> findAllByClientId(Long id);
    List<Appointment> findAllByClientIdAndProviderUser(Long id, User user);
    List<Appointment> findAllByClientIdAndClientUser(Long id, User user);

    Optional<Appointment> findByIdAndClientUser(Long id, User user);
    Optional<Appointment> findByIdAndProviderUser(Long id, User user);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Appointment a " +
            "WHERE a.room.id = :roomId " +
            "AND a.startTime < :end " +
            "AND a.endTime > :start")
    boolean existsByRoomIdAndTimeOverlap(
            @Param("roomId") Long roomId,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end
    );


    List<Appointment> findAllByProviderIdAndStartTimeBetween(Long providerId, Timestamp startDate, Timestamp endDate);


}
