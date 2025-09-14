package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.CreateServiceRequest;
import dev.marko.MedRecords.dtos.ServiceDto;
import dev.marko.MedRecords.dtos.UpdateServiceRequest;
import dev.marko.MedRecords.services.ServiceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping("/{providerId}/provider")
    public ResponseEntity<List<ServiceDto>> findServicesForProvider(@PathVariable Long providerId){

        var serviceListDto = serviceService.findServicesForProvider(providerId);
        return ResponseEntity.ok(serviceListDto);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDto> findService(@PathVariable Long id){

        var serviceDto = serviceService.findService(id);
        return ResponseEntity.ok(serviceDto);

    }

    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @PostMapping
    public ResponseEntity<ServiceDto> createService(@RequestBody @Valid CreateServiceRequest request,
                                                    UriComponentsBuilder builder){

        var serviceDto = serviceService.createService(request);
        var uri = builder.path("services/{id}").buildAndExpand(serviceDto.getId()).toUri();

        return ResponseEntity.created(uri).body(serviceDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDto> updateService(@RequestBody @Valid UpdateServiceRequest request,
                                                    @PathVariable Long id) {

        var serviceDto = serviceService.updateService(request, id);
        return ResponseEntity.ok(serviceDto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id){

        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();

    }

}
