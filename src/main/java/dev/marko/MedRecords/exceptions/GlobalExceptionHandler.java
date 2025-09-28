package dev.marko.MedRecords.exceptions;

import dev.marko.MedRecords.dtos.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AppointmentNotFoundException.class,
            ClientNotFoundException.class,
            InventoryNotFoundException.class,
            MedicalRecordNotFoundException.class,
            PhoneNumberNotFoundException.class,
            PhotoNotFoundException.class,
            ProviderNotFoundException.class,
            RoomNotFoundException.class,
            UserNotFoundException.class,
            ServiceNotFoundException.class,
            SmsMessageNotFoundException.class
    })
    public ResponseEntity<ErrorDto> handleNotFoundExceptions(RuntimeException ex) {
        ErrorDto response = new ErrorDto(
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // fallback:

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        ErrorDto response = new ErrorDto(
                "Unexpected error: " + ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

    }
}