package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.auth.RegisterClientRequest;
import dev.marko.MedRecords.dtos.ClientDto;
import dev.marko.MedRecords.dtos.UpdateClientRequest;
import dev.marko.MedRecords.services.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClientDto>> findClients(){

        var clientListDto = clientService.findClients();
        return ResponseEntity.ok(clientListDto);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> findClient(@PathVariable Long id){

        var clientDto = clientService.findClient(id);
        return ResponseEntity.ok(clientDto);

    }

    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody RegisterClientRequest request,
                                                  UriComponentsBuilder builder) {

        var clientDto = clientService.registerClient(request);
        var uri = builder.path("/clients/{id}").buildAndExpand(clientDto.getId()).toUri();

        return ResponseEntity.created(uri).body(clientDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long id, @RequestBody UpdateClientRequest request)  {

        var clientDto = clientService.updateClient(request, id);
        return ResponseEntity.ok(clientDto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id){

        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();

    }


}
