package dev.marko.MedRecords.calendar;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@AllArgsConstructor
@RestController
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/{providerId}")
    public ResponseEntity<ProviderCalendarDto> getProviderCalendar(@PathVariable Long providerId,
                                                                   @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                   @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){

        var providerCalendarDto = calendarService.getProviderCalendar(providerId, startDate, endDate);
        return ResponseEntity.ok(providerCalendarDto);

    }

}
