package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.ClientDto;
import dev.marko.MedRecords.dtos.UpdateClientRequest;
import dev.marko.MedRecords.exceptions.ClientNotFoundException;
import dev.marko.MedRecords.mappers.ClientMapper;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/admin/clients")
public class AdminClientController {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClientDto>> findClients(){

        var clientList = clientRepository.findAll();

        var clientListDto = clientMapper.toListDto(clientList);

        return ResponseEntity.ok(clientListDto);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> findClient(@PathVariable Long id){

        var client = clientRepository.findById(id).orElseThrow(ClientNotFoundException::new);
        var clientDto = clientMapper.toDto(client);

        return ResponseEntity.ok(clientDto);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long id, @RequestBody UpdateClientRequest request) throws AccessDeniedException {

        var client = clientRepository.findById(id).orElseThrow(ClientNotFoundException::new);

        clientMapper.update(request, client);

        clientRepository.save(client);

        var clientDto = clientMapper.toDto(client);

        return ResponseEntity.ok(clientDto);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id){

        var client = clientRepository.findById(id)
                .orElseThrow(ClientNotFoundException::new);

        userRepository.delete(client.getUser());

        return ResponseEntity.noContent().build();

    }


}
