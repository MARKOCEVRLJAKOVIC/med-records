package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.CreateMedicalRecordRequest;
import dev.marko.MedRecords.dtos.MedicalRecordDto;
import dev.marko.MedRecords.dtos.UpdateMedicalRecordRequest;
import dev.marko.MedRecords.exceptions.ClientNotFoundException;
import dev.marko.MedRecords.exceptions.MedicalRecordNotFoundException;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.exceptions.ServiceNotFoundException;
import dev.marko.MedRecords.mappers.MedicalRecordMapper;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.MedicalRecordRepository;
import dev.marko.MedRecords.repositories.ProviderRepository;
import dev.marko.MedRecords.repositories.ServiceRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/medical-records")
public class MedicalRecordController {

    private final AuthService authService;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final ClientRepository clientRepository;
    private final ProviderRepository providerRepository;
    private final ServiceRepository serviceRepository;

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> findMedicalRecord(@PathVariable Long id){

        var user = authService.getCurrentUser();

        var medicalRecord = switch (user.getRole()) {
            case CLIENT -> medicalRecordRepository.findByIdAndClientUser(id, user)
                    .orElseThrow(MedicalRecordNotFoundException::new);
            case PROVIDER -> medicalRecordRepository.findByIdAndProviderUser(id, user)
                    .orElseThrow(MedicalRecordNotFoundException::new);
            case ADMIN -> medicalRecordRepository.findById(id)
                    .orElseThrow(MedicalRecordNotFoundException::new);
            default -> throw new AccessDeniedException("Not authorized");
        };

        var medicalRecordDto = medicalRecordMapper.toDto(medicalRecord);

        return ResponseEntity.ok(medicalRecordDto);

    }

    @GetMapping("/{clientId}/client")
    public ResponseEntity<List<MedicalRecordDto>> findMedicalRecordsForClient(@PathVariable Long clientId){

        var user = authService.getCurrentUser();

        var client = clientRepository.findByIdAndUser(clientId, user)
                .orElseThrow(ClientNotFoundException::new);

        var medicalRecordList = medicalRecordRepository.findAllByClient(client);

        var medicalRecordListDto = medicalRecordMapper.toListDto(medicalRecordList);

        return ResponseEntity.ok(medicalRecordListDto);

    }

    @GetMapping("/{providerId}/provider")
    public ResponseEntity<List<MedicalRecordDto>> findMedicalRecordsForProvider(@PathVariable Long providerId){

        var user = authService.getCurrentUser();

        var provider = switch (user.getRole()) {
            case ADMIN -> providerRepository.findById(providerId).orElseThrow(ProviderNotFoundException::new);
            case PROVIDER -> providerRepository.findByIdAndUser(providerId, user)
                    .orElseThrow(ProviderNotFoundException::new);
            default -> throw new AccessDeniedException("Only provider see his/her medical records.");
        };

        var medicalRecordList = medicalRecordRepository.findAllByProvider(provider);

        var medicalRecordListDto = medicalRecordMapper.toListDto(medicalRecordList);

        return ResponseEntity.ok(medicalRecordListDto);

    }

    @PostMapping
    public ResponseEntity<MedicalRecordDto> createMedicalRecord(@RequestBody @Valid CreateMedicalRecordRequest request,
                                                                UriComponentsBuilder builder){

        var user = authService.getCurrentUser();

        var client = clientRepository.findById(request.getClientId())
                .orElseThrow(ClientNotFoundException::new);

        var service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(ServiceNotFoundException::new);

        var provider = providerRepository.findByIdAndUser(request.getProviderId(), user)
                .orElseThrow(ProviderNotFoundException::new);


        var medicalRecord = medicalRecordMapper.toEntity(request);

        medicalRecord.setClient(client);
        medicalRecord.setService(service);
        medicalRecord.setProvider(provider);

        medicalRecordRepository.save(medicalRecord);

        var medicalRecordDto = medicalRecordMapper.toDto(medicalRecord);

        var uri = builder.path("/medical-records/{id}").buildAndExpand(medicalRecordDto.getId()).toUri();

        return ResponseEntity.created(uri).body(medicalRecordDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> updateMedicalRecord(@RequestBody @Valid UpdateMedicalRecordRequest request,
                                                                @PathVariable Long id) {


        var user = authService.getCurrentUser();

        var medicalRecord = switch (user.getRole()) {
            case CLIENT -> medicalRecordRepository.findByIdAndClientUser(id, user)
                    .orElseThrow(MedicalRecordNotFoundException::new);
            case PROVIDER -> medicalRecordRepository.findByIdAndProviderUser(id, user)
                    .orElseThrow(MedicalRecordNotFoundException::new);
            case ADMIN -> medicalRecordRepository.findById(id)
                    .orElseThrow(MedicalRecordNotFoundException::new);
            default -> throw new AccessDeniedException("Not authorized");
        };


        medicalRecordMapper.update(request, medicalRecord);
        medicalRecordRepository.save(medicalRecord);

        var medicalRecordDto = medicalRecordMapper.toDto(medicalRecord);

        return ResponseEntity.ok(medicalRecordDto);

    }


}
