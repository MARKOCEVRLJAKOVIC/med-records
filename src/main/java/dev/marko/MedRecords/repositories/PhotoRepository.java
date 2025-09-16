package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Photo;
import dev.marko.MedRecords.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Optional<Photo> findByIdAndClient_User(Long id, User user);

}
