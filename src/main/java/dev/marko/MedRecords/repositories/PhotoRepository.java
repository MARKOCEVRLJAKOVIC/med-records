package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.Photo;
import dev.marko.MedRecords.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Optional<Photo> findByIdAndClient_User(Long id, User user);
    @Query("SELECT p FROM Photo p JOIN FETCH p.medicalRecord m JOIN FETCH m.provider WHERE p.id = :id AND m.provider.id = :providerId")
    Optional<Photo> findByIdWithMedicalRecordAndProviderId(@Param("id") Long id, @Param("providerId") Long providerId);

    @Query("SELECT p FROM Photo p JOIN FETCH p.medicalRecord m JOIN FETCH m.provider WHERE p.id = :id AND m.provider.id = :providerId")
    List<Photo> findAllByIdWithMedicalRecordAndProviderId(@Param("id") Long id, @Param("providerId") Long providerId);
    @Query("SELECT p FROM Photo p JOIN FETCH p.medicalRecord m WHERE m.client = :client")
    List<Photo> findAllForClient(@Param("client") Client client);

    List<Photo> findAllByIdAndClient_User(Long id, User user);



}
