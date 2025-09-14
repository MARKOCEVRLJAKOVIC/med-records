package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Role;
import dev.marko.MedRecords.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    List<User> findAllByRole(Role role);

}
