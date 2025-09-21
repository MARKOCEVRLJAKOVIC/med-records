package dev.marko.MedRecords.repositories;

import dev.marko.MedRecords.entities.Inventory;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByIdAndProvider(Long id, Provider provider);
    @Query("SELECT i FROM Inventory i WHERE i.id = :id AND i.provider.user = :user")
    Optional<Inventory> findByIdAndProviderUser(@Param("id") Long id, @Param("user") User user);


    List<Inventory> findAllByProviderId(Long providerId);
    List<Inventory> findAllByIdAndProvider(Long id, Provider provider);
    @Query("SELECT i FROM Inventory i WHERE i.provider.user = :user")
    List<Inventory> findAllByProviderUser(@Param("user") User user);


}
