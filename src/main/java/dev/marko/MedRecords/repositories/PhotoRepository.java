package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
