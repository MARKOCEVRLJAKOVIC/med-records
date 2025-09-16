package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.AppointmentDto;
import dev.marko.MedRecords.dtos.BookAppointmentRequest;
import dev.marko.MedRecords.dtos.UpdateAppointmentRequest;
import dev.marko.MedRecords.services.AppointmentBookingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentBookingService appointmentService;

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointment(@PathVariable Long id) {

        var appointmentDto = appointmentService.getAppointment(id);
        return ResponseEntity.ok(appointmentDto);

    }

    @GetMapping("/{providerId}/provider")
    public ResponseEntity<List<AppointmentDto>> findAppointmentsForProvider(@PathVariable Long providerId) {

        var appointmentListDto = appointmentService.findAppointmentsForProvider(providerId);
        return ResponseEntity.ok(appointmentListDto);

    }

    @GetMapping("/{clientId}/client")
    public ResponseEntity<List<AppointmentDto>> findAppointmentsForClient(@PathVariable Long clientId) {

        var appointmentDtoList = appointmentService.findAppointmentsForClient(clientId);
        return ResponseEntity.ok(appointmentDtoList);

    }

    @PostMapping
    public ResponseEntity<AppointmentDto> bookAppointment(@RequestBody @Valid BookAppointmentRequest request,
                                                          UriComponentsBuilder builder) {

        var appointmentDto = appointmentService.bookAppointment(request);
        var uri = builder.path("appointments/{id}").buildAndExpand(appointmentDto.getId()).toUri();

        return ResponseEntity.created(uri).body(appointmentDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto> updateAppointment(@PathVariable Long id,
                                                            @RequestBody @Valid UpdateAppointmentRequest request){

        var appointmentDto = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(appointmentDto);

    }




}
