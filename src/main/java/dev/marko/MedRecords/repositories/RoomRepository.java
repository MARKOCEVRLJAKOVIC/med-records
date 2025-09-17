package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
