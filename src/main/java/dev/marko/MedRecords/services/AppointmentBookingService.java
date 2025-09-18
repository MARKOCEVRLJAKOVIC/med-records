package dev.marko.MedRecords.services;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.AppointmentDto;
import dev.marko.MedRecords.dtos.BookAppointmentRequest;
import dev.marko.MedRecords.dtos.UpdateAppointmentRequest;
import dev.marko.MedRecords.entities.*;
import dev.marko.MedRecords.exceptions.*;
import dev.marko.MedRecords.mappers.AppointmentMapper;
import dev.marko.MedRecords.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
    private final RoomRepository roomRepository;

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
            case PROVIDER -> appointmentRepository.findAllByClientIdAndProviderUser(clientId, user);
            case CLIENT -> appointmentRepository.findAllByClientIdAndClientUser(clientId, user);
            default -> throw new AccessDeniedException("");
        };

        return appointmentMapper.toListDto(appointmentList);

    }

    @Transactional
    public AppointmentDto bookAppointment(BookAppointmentRequest request){

        var user = authService.getCurrentUser();
        var client = findClient(request, user);
        var provider = findProvider(request, user);


        var appointment = appointmentMapper.toEntity(request);
        appointment.setClient(client);
        appointment.setProvider(provider);

        // room check
        if (request.getRoomId() != null) {
            var room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(RoomNotFoundException::new);

            if (!room.getProvider().getId().equals(provider.getId())) {
                throw new AccessDeniedException("Room does not belong to this provider.");
            }

            if (!isRoomAvailable(room.getId(), request.getStartTime(), request.getEndTime())) {
                throw new IllegalStateException("Room is already booked in this time slot.");
            }

            appointment.setRoom(room);
        }

        // services

        var appointmentServices = buildAppointmentServices(appointment, request.getServiceIds());

        appointment.setServices(appointmentServices);
        appointmentRepository.save(appointment);
        return appointmentMapper.toDto(appointment);

    }

    @Transactional
    public AppointmentDto updateAppointment(Long id, UpdateAppointmentRequest request){

        var user = authService.getCurrentUser();

        if (request.getStartTime().after(request.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time.");
        }

        var appointment = getAppointmentByUserRole(id,user);


        // update room if changed
        if (request.getRoomId() != null) {
            var room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(RoomNotFoundException::new);
            if (!room.getProvider().getId().equals(appointment.getProvider().getId())) {
                throw new AccessDeniedException("Room does not belong to this provider.");
            }
            if (!isRoomAvailable(room.getId(), request.getStartTime(), request.getEndTime())) {
                throw new IllegalStateException("Room is already booked in this time slot.");
            }
            appointment.setRoom(room);
        }

        // update services if provided
        if (request.getServiceIds() != null) {

            var appointmentServices = buildAppointmentServices(appointment, request.getServiceIds());
            appointment.setServices(appointmentServices);

        }

        appointmentMapper.update(request, appointment);
        appointmentRepository.save(appointment);
        return appointmentMapper.toDto(appointment);

    }

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

    public boolean isRoomAvailable(Long roomId, Timestamp start, Timestamp end) {
        return !appointmentRepository.existsByRoomIdAndTimeOverlap(roomId, start, end);
    }

    private List<AppointmentService> buildAppointmentServices(Appointment appointment, List<Long> serviceIds) {
        return serviceIds.stream()
                .map(serviceId -> {
                    var service = serviceRepository.findById(serviceId)
                            .orElseThrow(ServiceNotFoundException::new);
                    return AppointmentService.builder()
                            .appointment(appointment)
                            .service(service)
                            .durationOverride(service.getDurationMinutes())
                            .priceOverride(service.getPrice())
                            .build();
                })
                .toList();
    }



}
