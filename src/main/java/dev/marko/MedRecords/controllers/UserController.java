package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.UpdateClientRequest;
import dev.marko.MedRecords.dtos.UserDto;
import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.Role;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.UserNotFoundException;
import dev.marko.MedRecords.mappers.ClientMapper;
import dev.marko.MedRecords.mappers.UserMapper;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ClientMapper clientMapper;
    private final ClientRepository clientRepository;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) throws AccessDeniedException {

        var currentUser = authService.getCurrentUser();

        var user = switch (currentUser.getRole()) {
            case CLIENT -> userRepository.findById(id)
                    .filter(u -> u.getId().equals(currentUser.getId())) // client can view only their profile
                    .orElseThrow(UserNotFoundException::new);
            case ADMIN -> userRepository.findById(id) // admin can view any profile just by id
                    .orElseThrow(UserNotFoundException::new);
            default -> throw new AccessDeniedException("Unauthorized");
        };

        var userDto = userMapper.toDto(user);

        return ResponseEntity.ok(userDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateClient(@PathVariable Long id, @RequestBody UpdateClientRequest request) throws AccessDeniedException {

        var current = authService.getCurrentUser();

        if (!current.getId().equals(id)) {
            throw new  AccessDeniedException("You can't change profile of other users.");
        }

        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        Client client = user.getClient();
        if (client == null) {
            client = new Client();
            client.setUser(user);
            user.setClient(client);
        }

        userMapper.updateClient(request, user);

        clientMapper.update(request, client);

        var saved = userRepository.save(user);

        var userDto = userMapper.toDto(saved);

        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) throws AccessDeniedException {

        var currentUser = authService.getCurrentUser();

        var user = switch (currentUser.getRole()) {
            case CLIENT -> userRepository.findById(id)
                    .filter(u -> u.getId().equals(currentUser.getId()))
                    .orElseThrow(UserNotFoundException::new);
            case ADMIN, PROVIDER -> userRepository.findById(id)
                    .orElseThrow(UserNotFoundException::new);
            default -> throw new AccessDeniedException("Unauthorized");
        };

        checkDeleteRules(currentUser, user);

        userRepository.delete(user);

        return ResponseEntity.noContent().build();

    }

    private void checkDeleteRules(User currentUser, User targetUser) throws AccessDeniedException {
        if (currentUser.getRole() == Role.ADMIN && targetUser.getRole() == Role.ADMIN
                && !currentUser.getId().equals(targetUser.getId())) {
            throw new AccessDeniedException("Admin cannot delete another admin");
        }
        if (currentUser.getRole() == Role.PROVIDER) {
            if (targetUser.getRole() == Role.PROVIDER && !currentUser.getId().equals(targetUser.getId())) {
                throw new AccessDeniedException("Provider cannot delete another provider");
            }
            if (targetUser.getRole() == Role.ADMIN) {
                throw new AccessDeniedException("Provider cannot delete an admin");
            }
        }
    }



}
