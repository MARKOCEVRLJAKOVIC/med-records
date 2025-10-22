package dev.marko.MedRecords.services;

import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.ClientNotFoundException;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.ProviderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleAuthentication {

    private final ProviderRepository providerRepository;
    private final ClientRepository clientRepository;

    public Client findClientForRole(Long id, User user) {
        return switch (user.getRole()) {

            case ADMIN -> clientRepository.findById(id).orElseThrow(ClientNotFoundException::new);
            case PROVIDER -> clientRepository.findByIdAndProviderUser(id, user)
                    .orElseThrow(ClientNotFoundException::new);
            case CLIENT -> clientRepository.findByIdAndUser(id, user)
                    .orElseThrow(ClientNotFoundException::new);
            default -> throw new AccessDeniedException("You can only view your clients.");

        };
    }

    public Provider getProviderForRole(Long providerId, User user) {
        return switch (user.getRole()) {
            case ADMIN -> providerRepository.findById(providerId)
                    .orElseThrow(ProviderNotFoundException::new);
            case PROVIDER -> providerRepository.findByIdAndUser(providerId, user)
                    .orElseThrow(ProviderNotFoundException::new);
            default -> throw new AccessDeniedException("Access Denied");
        };
    }

}
