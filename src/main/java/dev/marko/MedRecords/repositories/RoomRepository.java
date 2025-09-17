package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.Room;
import dev.marko.MedRecords.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByIdAndProviderUser(Long id, User user);

    List<Room> findAllByProvider(Provider provider);

}
