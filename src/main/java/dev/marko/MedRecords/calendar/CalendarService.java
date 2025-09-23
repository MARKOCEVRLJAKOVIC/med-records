package dev.marko.MedRecords.calendar;

import dev.marko.MedRecords.entities.Appointment;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.SlotStatus;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.mappers.CalendarSlotMapper;
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
    private final CalendarSlotMapper calendarSlotMapper;

    public ProviderCalendarDto getProviderCalendar(Long providerId, LocalDate startDate, LocalDate endDate) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(ProviderNotFoundException::new);

        // Fetch all appointments for this provider within the date range
        List<Appointment> appointments = appointmentRepository
                .findAllByProviderIdAndStartTimeBetween(providerId,
                        java.sql.Timestamp.valueOf(startDate.atStartOfDay()),
                        java.sql.Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));

        // Group appointments by date
        Map<LocalDate, List<Appointment>> appointmentsByDate = appointments.stream()
                .collect(Collectors.groupingBy(appt -> appt.getStartTime().toLocalDateTime().toLocalDate()));

        // Build calendar days
        List<CalendarDayDto> days = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Appointment> dayAppointments = appointmentsByDate.getOrDefault(date, Collections.emptyList());

            // Build slots
            List<CalendarSlot> slots = buildSlots(date, dayAppointments);

            List<TimeSlotDto> slotDtos = mapToDtoList(slots);

            CalendarDayDto dayDto = new CalendarDayDto();
            dayDto.setDate(date);
            dayDto.setAvailable(slots.stream().anyMatch(s -> s.getStatus() == SlotStatus.AVAILABLE));
            dayDto.setCalendarSlots(slots);

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

    private List<CalendarSlot> buildSlots(LocalDate date, List<Appointment> appointments) {
        List<CalendarSlot> slots = new ArrayList<>();

        // Define working hours (example: 09:00 - 17:00)
        LocalTime workStart = LocalTime.of(9, 0);
        LocalTime workEnd = LocalTime.of(17, 0);

        // Sort appointments by start time
        appointments.sort(Comparator.comparing(Appointment::getStartTime));

        LocalTime current = workStart;
        for (Appointment appt : appointments) {
            LocalTime apptStart = appt.getStartTime().toLocalDateTime().toLocalTime();
            LocalTime apptEnd = appt.getEndTime().toLocalDateTime().toLocalTime();

            // Free slot before appointment
            if (apptStart.isAfter(current)) {
                slots.add(buildSlot(date, current, apptStart, SlotStatus.AVAILABLE, null));
            }

            // Booked slot
            slots.add(buildSlot(date, apptStart, apptEnd, SlotStatus.BOOKED, appt));

            current = apptEnd;
        }

        // Free slot after last appointment
        if (current.isBefore(workEnd)) {
            slots.add(buildSlot(date, current, workEnd, SlotStatus.AVAILABLE, null));
        }

        return slots;
    }

    private CalendarSlot buildSlot(LocalDate date, LocalTime start, LocalTime end, SlotStatus status, Appointment appointment) {
        CalendarSlot slot = new CalendarSlot();
        slot.setDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setStatus(status);
        slot.setAppointment(appointment);
        slot.setClientName(appointment != null && appointment.getClient() != null
                ? appointment.getClient().getFirstName() + " " + appointment.getClient().getLastName()
                : null);
        slot.setProvider(appointment != null ? appointment.getProvider() : null);
        return slot;
    }

    private TimeSlotDto mapToDto(CalendarSlot slot) {
        TimeSlotDto dto = new TimeSlotDto();
        dto.setStartTime(slot.getStartTime().toString());
        dto.setEndTime(slot.getEndTime().toString());
        dto.setStatus(slot.getStatus().name());
        dto.setAppointmentId(slot.getAppointment() != null ? slot.getAppointment().getId() : null);
        dto.setClientName(slot.getClientName());
        return dto;
    }

    private List<TimeSlotDto> mapToDtoList(List<CalendarSlot> slots) {
        return slots.stream()
                .map(this::mapToDto)
                .toList();
    }
}
