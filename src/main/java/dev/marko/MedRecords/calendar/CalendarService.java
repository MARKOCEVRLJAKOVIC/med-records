package dev.marko.MedRecords.calendar;

import dev.marko.MedRecords.entities.Appointment;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.SlotStatus;
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
                .findAllByProviderIdAndStartTimeBetween(
                        providerId,
                        java.sql.Timestamp.valueOf(startDate.atStartOfDay()),
                        java.sql.Timestamp.valueOf(endDate.plusDays(1).atStartOfDay())
                );

        // Group appointments by date
        Map<LocalDate, List<Appointment>> appointmentsByDate = appointments.stream()
                .collect(Collectors.groupingBy(appt -> appt.getStartTime().toLocalDateTime().toLocalDate()));

        // Build calendar days
        List<CalendarDayDto> days = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Appointment> dayAppointments = appointmentsByDate.getOrDefault(date, Collections.emptyList());

            List<TimeSlotDto> slots = buildTimeSlots(provider, date, dayAppointments);

            CalendarDayDto dayDto = new CalendarDayDto();
            dayDto.setDate(date);
            dayDto.setAvailable(slots.stream().anyMatch(s -> SlotStatus.AVAILABLE.name().equals(s.getStatus())));
            dayDto.setTimeSlotDtoList(slots);

            days.add(dayDto);
        }

        // Build provider calendar DTO
        ProviderCalendarDto providerCalendarDto = new ProviderCalendarDto();
        providerCalendarDto.setProviderId(provider.getId());
        providerCalendarDto.setProviderName(provider.getFirstName() + " " + provider.getLastName());
        providerCalendarDto.setSpecialty(provider.getSpecialty());
        providerCalendarDto.setDaysDto(days);

        return providerCalendarDto;
    }


    private List<TimeSlotDto> buildTimeSlots(Provider provider, LocalDate date, List<Appointment> appointments) {
        List<TimeSlotDto> slots = new ArrayList<>();

        LocalTime workStart = Optional.ofNullable(provider.getWorkStart()).orElse(LocalTime.of(9, 0));
        LocalTime workEnd = Optional.ofNullable(provider.getWorkEnd()).orElse(LocalTime.of(17, 0));

        // Sort appointments by start time
        appointments.sort(Comparator.comparing(Appointment::getStartTime));

        LocalTime current = workStart;
        for (Appointment appt : appointments) {
            LocalTime apptStart = appt.getStartTime().toLocalDateTime().toLocalTime();
            LocalTime apptEnd = appt.getEndTime().toLocalDateTime().toLocalTime();

            // Free slot before appointment
            if (apptStart.isAfter(current)) {
                slots.add(createTimeSlotDto(current, apptStart, SlotStatus.AVAILABLE, null));
            }

            // Booked slot
            slots.add(createTimeSlotDto(apptStart, apptEnd, SlotStatus.BOOKED, appt));

            current = apptEnd;
        }

        // Free slot after last appointment
        if (current.isBefore(workEnd)) {
            slots.add(createTimeSlotDto(current, workEnd, SlotStatus.AVAILABLE, null));
        }

        return slots;
    }

    private TimeSlotDto createTimeSlotDto(LocalTime start, LocalTime end, SlotStatus status, Appointment appointment) {
        TimeSlotDto dto = new TimeSlotDto();
        dto.setStartTime(start.toString());
        dto.setEndTime(end.toString());
        dto.setStatus(status.name());

        if (appointment != null) {
            dto.setAppointmentId(appointment.getId());
            if (appointment.getClient() != null) {
                dto.setClientName(
                        Optional.ofNullable(appointment.getClient().getFirstName()).orElse("") + " " +
                                Optional.ofNullable(appointment.getClient().getLastName()).orElse("")
                );
            }
        }

        return dto;
    }
}
