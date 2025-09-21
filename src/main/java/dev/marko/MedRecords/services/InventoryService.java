package dev.marko.MedRecords.services;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.CreateInventoryRequest;
import dev.marko.MedRecords.dtos.InventoryDto;
import dev.marko.MedRecords.dtos.UpdateInventoryRequest;
import dev.marko.MedRecords.entities.Inventory;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.Role;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.InventoryNotFoundException;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.mappers.InventoryMapper;
import dev.marko.MedRecords.repositories.InventoryRepository;
import dev.marko.MedRecords.repositories.ProviderRepository;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class InventoryService {

    private final AuthService authService;
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProviderRepository providerRepository;


    public List<InventoryDto> findAllInventoriesForProvider(Long providerId){

        var user = authService.getCurrentUser();

        var inventoryList = switch (user.getRole()){
            case ADMIN -> inventoryRepository.findAllByProviderId(providerId);
            case PROVIDER -> inventoryRepository.findAllByProviderUser(user);
            default -> throw new AccessDeniedException("Access denied");
        };

        return inventoryMapper.toListDto(inventoryList);

    }

    public InventoryDto findInventory(Long id){

        var user = authService.getCurrentUser();

        var inventory = getInventoryForRole(id, user);

        return inventoryMapper.toDto(inventory);

    }

    @Transactional
    public InventoryDto createInventory(CreateInventoryRequest request){

        var user = authService.getCurrentUser();

        var provider = getProviderForRole(request, user);

        var inventory = inventoryMapper.toEntity(request);
        inventory.setProvider(provider);

        inventoryRepository.save(inventory);

        return inventoryMapper.toDto(inventory);

    }

    @Transactional
    public InventoryDto updateInventory(Long id, UpdateInventoryRequest request){

        var user = authService.getCurrentUser();
        var inventory = getInventoryForRole(id, user);

        inventoryMapper.update(request, inventory);
        inventoryRepository.save(inventory);

        return inventoryMapper.toDto(inventory);

    }

    @Transactional
    public void deleteInventory(Long id){

        var user = authService.getCurrentUser();
        var inventory = getInventoryForRole(id, user);

        inventoryRepository.delete(inventory);

    }

    private Inventory getInventoryForRole(Long id, User user) {
        return switch (user.getRole()) {

            case ADMIN -> inventoryRepository.findById(id)
                    .orElseThrow(InventoryNotFoundException::new);
            case PROVIDER -> inventoryRepository.findByIdAndProviderUser(id, user)
                    .orElseThrow(InventoryNotFoundException::new);
            default -> throw new AccessDeniedException("Access denied");

        };
    }

    private Provider getProviderForRole(CreateInventoryRequest request, User user) {
        return switch (user.getRole()) {
            case ADMIN -> providerRepository.findById(request.getProviderId())
                    .orElseThrow(ProviderNotFoundException::new);
            case PROVIDER -> providerRepository.findByIdAndUser(request.getProviderId(), user)
                    .orElseThrow(ProviderNotFoundException::new);
            default -> throw new AccessDeniedException("Access Denied");
        };
    }

}
