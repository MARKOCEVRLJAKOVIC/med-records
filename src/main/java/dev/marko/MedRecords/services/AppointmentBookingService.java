package dev.marko.MedRecords.services;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.AppointmentDto;
import dev.marko.MedRecords.dtos.BookAppointmentRequest;
import dev.marko.MedRecords.dtos.UpdateAppointmentRequest;
import dev.marko.MedRecords.entities.*;
import dev.marko.MedRecords.exceptions.AppointmentNotFoundException;
import dev.marko.MedRecords.exceptions.ClientNotFoundException;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.exceptions.ServiceNotFoundException;
import dev.marko.MedRecords.mappers.AppointmentMapper;
import dev.marko.MedRecords.repositories.AppointmentRepository;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.ProviderRepository;
import dev.marko.MedRecords.repositories.ServiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for managing appointments.
 * Responsibilities:
 *  - Retrieve appointments by ID with access control based on user role (ADMIN, PROVIDER, CLIENT).
 *  - Create and update appointments with ownership checks for clients and providers.
 *  - Fetch lists of appointments for providers or clients with appropriate access restrictions.
 *  - Encapsulates database access and ownership logic, keeping the controller clean and focused on HTTP flow.
 * Helper methods:
 *  - getAppointmentByUserRole(...) → centralizes appointment retrieval with role-based access checks.
 *  - findClient(...) and findProvider(...) → fetch entities with ownership verification based on user role.
 *  - findService(...) → fetch service entity (no ownership check needed).
 * Author: Marko
 */

@AllArgsConstructor
@Service
public class AppointmentBookingService {

    private final ProviderRepository providerRepository;
    private final AuthService authService;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final ClientRepository clientRepository;
    private final ServiceRepository serviceRepository;

    public AppointmentDto getAppointment(Long id){

        var user = authService.getCurrentUser();

        var appointment = getAppointmentByUserRole(id, user);

        return appointmentMapper.toDto(appointment);

    }


    public List<AppointmentDto> findAppointmentsForProvider(Long providerId){

        var user = authService.getCurrentUser();

        var provider = providerRepository.findById(providerId).orElseThrow(ProviderNotFoundException::new);

        var appointmentList = switch (user.getRole()) {
            case ADMIN -> appointmentRepository.findAllByProviderId(providerId);
            case PROVIDER -> {
                if (!provider.getUser().getId().equals(user.getId())) {
                    throw new AccessDeniedException("You cannot access other providers' appointments.");
                }
                yield appointmentRepository.findAllByProviderId(providerId);
            }
            default -> throw new AccessDeniedException("Unauthorized");
        };

        return appointmentMapper.toListDto(appointmentList);

    }

    public List<AppointmentDto> findAppointmentsForClient(Long clientId){

        var user = authService.getCurrentUser();

        var appointmentList = switch (user.getRole()) {
            case ADMIN -> appointmentRepository.findAllByClientId(clientId);
            case PROVIDER -> appointmentRepository.findAllByClientIdAndProviderUser(clientId, user).orElseThrow();
            case CLIENT -> appointmentRepository.findAllByClientIdAndClientUser(clientId, user).orElseThrow();
            default -> throw new AccessDeniedException("");
        };

        return appointmentMapper.toListDto(appointmentList);

    }

    @Transactional
    public AppointmentDto bookAppointment(BookAppointmentRequest request){

        var user = authService.getCurrentUser();

        var client = findClient(request, user);
        var provider = findProvider(request, user);
        var service = findService(request);



        var appointment = appointmentMapper.toEntity(request);
        appointment.setClient(client);
        appointment.setProvider(provider);

        AppointmentService services = AppointmentService.builder()
                .service(service)
                .durationOverride(service.getDurationMinutes())
                .appointment(appointment)
                .priceOverride(service.getPrice())
                .build();

        appointment.setServices(List.of(services));


        appointmentRepository.save(appointment);

        return appointmentMapper.toDto(appointment);

    }

    @Transactional
    public AppointmentDto updateAppointment(Long id, UpdateAppointmentRequest request){

        var user = authService.getCurrentUser();

        var appointment = getAppointmentByUserRole(id,user);

        appointmentMapper.update(request, appointment);
        appointmentRepository.save(appointment);

        return appointmentMapper.toDto(appointment);

    }

    // methods


    private Appointment getAppointmentByUserRole(Long id, User user) {
        return switch (user.getRole()) {
            case ADMIN -> appointmentRepository.findById(id)
                    .orElseThrow(AppointmentNotFoundException::new);
            case PROVIDER -> appointmentRepository.findByIdAndProviderUser(id, user)
                    .orElseThrow(AppointmentNotFoundException::new);
            case CLIENT -> appointmentRepository.findByIdAndClientUser(id, user)
                    .orElseThrow(AppointmentNotFoundException::new);
            default -> throw new AccessDeniedException("Only providers and clients can see appointments");
        };
    }

    private Client findClient(BookAppointmentRequest request, User user) {
        if (user.getRole() == Role.CLIENT) {
            return clientRepository.findByIdAndUser(request.getClientId(), user)
                    .orElseThrow(ClientNotFoundException::new);
        }
        return clientRepository.findById(request.getClientId())
                .orElseThrow(ClientNotFoundException::new);
    }

    private Provider findProvider(BookAppointmentRequest request, User user) {
        if (user.getRole() == Role.PROVIDER) {
            return providerRepository.findByIdAndUser(request.getProviderId(), user)
                    .orElseThrow(ProviderNotFoundException::new);
        }
        return providerRepository.findById(request.getProviderId())
                .orElseThrow(ProviderNotFoundException::new);
    }

    private dev.marko.MedRecords.entities.Service findService(BookAppointmentRequest request) {
        return serviceRepository.findById(request.getServiceId())
                .orElseThrow(ServiceNotFoundException::new);
    }




}
