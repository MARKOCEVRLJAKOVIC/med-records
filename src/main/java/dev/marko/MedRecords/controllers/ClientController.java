package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.auth.RegisterClientRequest;
import dev.marko.MedRecords.dtos.ClientDto;
import dev.marko.MedRecords.dtos.UpdateClientRequest;
import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.Role;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.ClientNotFoundException;
import dev.marko.MedRecords.mappers.ClientMapper;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/clients")
public class ClientController {

    private final AuthService authService;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ClientDto>> findClients(){

        var user = authService.getCurrentUser();
        var clientList = clientRepository.findAllByUser(user);

        var clientListDto = clientMapper.toListDto(clientList);

        return ResponseEntity.ok(clientListDto);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> findClient(@PathVariable Long id){

        var user = authService.getCurrentUser();
        var client = clientRepository.findByIdAndUser(id, user).orElseThrow(ClientNotFoundException::new);

        var clientDto = clientMapper.toDto(client);

        return ResponseEntity.ok(clientDto);

    }

    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody RegisterClientRequest request,
                                                  UriComponentsBuilder builder) {

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CLIENT)
                .build();

        var client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .allergies(request.getAllergies())
                .medicalNotes(request.getMedicalNotes())
                .user(user)
                .build();

        user.setClient(client);
        userRepository.save(user);
        var clientDto = clientMapper.toDto(client);

        var uri = builder.path("/clients/{id}").buildAndExpand(clientDto.getId()).toUri();

        return ResponseEntity.created(uri).body(clientDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long id, @RequestBody UpdateClientRequest request)  {

        var user = authService.getCurrentUser();

        var client = clientRepository.findByIdAndUser(id, user).orElseThrow(ClientNotFoundException::new);

        clientMapper.update(request, client);

        clientRepository.save(client);

        var clientDto = clientMapper.toDto(client);

        return ResponseEntity.ok(clientDto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id){

        var user = authService.getCurrentUser();

        var client = clientRepository.findByIdAndUser(id, user)
                .orElseThrow(ClientNotFoundException::new);

        userRepository.delete(client.getUser());

        return ResponseEntity.noContent().build();

    }


}
