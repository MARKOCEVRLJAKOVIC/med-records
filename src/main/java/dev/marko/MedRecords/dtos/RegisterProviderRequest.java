package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
public class RegisterProviderRequest {

    private String email;
    private String password;

    private String firstName;
    private String lastName;
    private String phone;
    private String specialty;
    private String licenseNumber;
    private LocalDate employmentStart;
    private LocalDate employmentEnd;


}
