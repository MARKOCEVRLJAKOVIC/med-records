package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.UpdateUserRequest;
import dev.marko.MedRecords.dtos.UserDto;
import dev.marko.MedRecords.mappers.UserMapper;
import dev.marko.MedRecords.repositories.UserRepository;
import dev.marko.MedRecords.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/clients")
    public ResponseEntity<List<UserDto>> getClientUsers(){

        var userListDto = userService.getClientUsers();
        return ResponseEntity.ok(userListDto);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/providers")
    public ResponseEntity<List<UserDto>> getProviderUsers(){

        var userListDto = userService.getProviderUsers();
        return ResponseEntity.ok(userListDto);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {

        var userDto = userService.getUser(id);
        return ResponseEntity.ok(userDto);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest request) {

        var userDto = userService.updateUser(request, id);
        return ResponseEntity.ok(userDto);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();

    }
}
