package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.CreateMedicalRecordRequest;
import dev.marko.MedRecords.dtos.MedicalRecordDto;
import dev.marko.MedRecords.dtos.UpdateMedicalRecordRequest;
import dev.marko.MedRecords.services.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping("/{clientId}/client")
    public ResponseEntity<List<MedicalRecordDto>> findMedicalRecordsForClient(@PathVariable Long clientId){

        var medicalRecordListDto = medicalRecordService.findMedicalRecordsForClient(clientId);
        return ResponseEntity.ok(medicalRecordListDto);

    }

    @GetMapping("/{providerId}/provider")
    public ResponseEntity<List<MedicalRecordDto>> findMedicalRecordsForProvider(@PathVariable Long providerId){

        var medicalRecordListDto = medicalRecordService.findMedicalRecordsForProvider(providerId);
        return ResponseEntity.ok(medicalRecordListDto);

    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> findMedicalRecord(@PathVariable Long id){

        var medicalRecordDto = medicalRecordService.findMedicalRecord(id);
        return ResponseEntity.ok(medicalRecordDto);

    }

    @PostMapping
    public ResponseEntity<MedicalRecordDto> createMedicalRecord(@RequestBody @Valid CreateMedicalRecordRequest request,
                                                                UriComponentsBuilder builder){

        var medicalRecordDto = medicalRecordService.createMedicalRecord(request);
        var uri = builder.path("/medical-records/{id}").buildAndExpand(medicalRecordDto.getId()).toUri();

        return ResponseEntity.created(uri).body(medicalRecordDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> updateMedicalRecord(@RequestBody @Valid UpdateMedicalRecordRequest request,
                                                                @PathVariable Long id) {

        var medicalRecordDto = medicalRecordService.updateMedicalRecord(request, id);
        return ResponseEntity.ok(medicalRecordDto);

    }

    public ResponseEntity<MedicalRecordDto> deleteMedicalRecord(Long id){

        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();

    }


}
