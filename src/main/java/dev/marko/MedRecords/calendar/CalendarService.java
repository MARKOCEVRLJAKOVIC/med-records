package dev.marko.MedRecords.calendar;

import dev.marko.MedRecords.entities.Appointment;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.repositories.AppointmentRepository;
import dev.marko.MedRecords.repositories.ProviderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CalendarService {

    private final ProviderRepository providerRepository;
    private final AppointmentRepository appointmentRepository;

    public ProviderCalendarDto getProviderCalendar(Long providerId, LocalDate startDate, LocalDate endDate) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(ProviderNotFoundException::new);

        // Fetch all appointments for this provider within the date range
        List<Appointment> appointments = appointmentRepository
                .findAllByProviderIdAndStartTimeBetween(providerId,
                        java.sql.Timestamp.valueOf(startDate.atStartOfDay()),
                        java.sql.Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));

        // Group appointments by date for easier calendar building
        Map<LocalDate, List<Appointment>> appointmentsByDate = appointments.stream()
                .collect(Collectors.groupingBy(appt -> appt.getStartTime().toLocalDateTime().toLocalDate()));

        // Build calendar days
        List<CalendarDayDto> days = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Appointment> dayAppointments = appointmentsByDate.getOrDefault(date, Collections.emptyList());

            // Build time slots for the day
            List<TimeSlotDto> timeSlots = buildTimeSlots(dayAppointments);

            boolean available = !timeSlots.isEmpty();

            CalendarDayDto dayDto = new CalendarDayDto();
            dayDto.setDate(date);
            dayDto.setAvailable(available);
            dayDto.setTimeSlots(timeSlots);

            days.add(dayDto);
        }

        // Build provider calendar DTO
        ProviderCalendarDto providerCalendarDto = new ProviderCalendarDto();
        providerCalendarDto.setProviderId(provider.getId());
        providerCalendarDto.setProviderName(provider.getFirstName() + " " + provider.getLastName());
        providerCalendarDto.setSpecialty(provider.getSpecialty());
        // providerCalendarDto.setPhotoUrl(provider.getPhotoUrl()); // ako imaš slike
        providerCalendarDto.setDaysDto(days);

        return providerCalendarDto;
    }

    private List<TimeSlotDto> buildTimeSlots(List<Appointment> appointments) {
        List<TimeSlotDto> slots = new ArrayList<>();

        // Define working hours (example: 09:00 - 17:00)
        LocalTime workStart = LocalTime.of(9, 0);
        LocalTime workEnd = LocalTime.of(17, 0);

        // Sort appointments by start time
        appointments.sort(Comparator.comparing(Appointment::getStartTime));

        LocalTime current = workStart;
        for (Appointment appt : appointments) {
            LocalTime apptStart = appt.getStartTime().toLocalDateTime().toLocalTime();
            LocalTime appointmentEnd = appt.getEndTime().toLocalDateTime().toLocalTime();

            // Free slot before appointment
            if (apptStart.isAfter(current)) {
                slots.add(buildSlot(current, apptStart, "AVAILABLE", null, null));
            }

            // Booked slot
            slots.add(buildSlot(apptStart, appointmentEnd, "BOOKED",
                    appt.getId(),
                    appt.getClient() != null ? appt.getClient().getFirstName() + " " + appt.getClient().getLastName() : null));

            current = appointmentEnd;
        }

        // Free slot after last appointment
        if (current.isBefore(workEnd)) {
            slots.add(buildSlot(current, workEnd, "AVAILABLE", null, null));
        }

        return slots;
    }

    private TimeSlotDto buildSlot(LocalTime start, LocalTime end, String status, Long appointmentId, String clientName) {
        TimeSlotDto slot = new TimeSlotDto();
        slot.setStartTime(start.toString());
        slot.setEndTime(end.toString());
        slot.setStatus(status);
        slot.setAppointmentId(appointmentId);
        slot.setClientName(clientName);
        return slot;
    }
}
