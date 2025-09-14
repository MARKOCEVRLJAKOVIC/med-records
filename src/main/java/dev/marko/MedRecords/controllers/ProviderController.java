package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.*;
import dev.marko.MedRecords.services.ProviderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/providers")
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping
    public ResponseEntity<List<ProviderDto>> findAllProviders(){

        var providerListDto = providerService.findAllProviders();
        return ResponseEntity.ok(providerListDto);

    }

    @GetMapping("/{providerId}/clients")
    public ResponseEntity<List<ClientDto>> clientsForProvider(@PathVariable Long providerId) {

        var clientListDto = providerService.clientsForProvider(providerId);
        return ResponseEntity.ok(clientListDto);

    }


    @GetMapping("/{id}")
    public ResponseEntity<ProviderDto> findProvider(@PathVariable Long id){

        var providerDto = providerService.findProvider(id);
        return ResponseEntity.ok(providerDto);

    }

    @PostMapping
    public ResponseEntity<ProviderDto> registerProvider(@RequestBody RegisterProviderRequest request,
                                                        UriComponentsBuilder builder){

        var providerDto = providerService.registerProvider(request);
        var uri = builder.path("/providers/{id}").buildAndExpand(providerDto.getId()).toUri();

        return ResponseEntity.created(uri).body(providerDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProviderDto> updateProvider(@RequestBody @Valid UpdateProviderRequest request,
                                                      @PathVariable Long id) {

        var providerDto = providerService.updateProvider(request, id);
        return ResponseEntity.ok(providerDto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id){

        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();

    }



}
