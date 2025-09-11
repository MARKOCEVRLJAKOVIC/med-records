package dev.marko.MedRecords.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProviderDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String specialty;
    private String licenseNumber;
    private LocalDate employmentStart;
    private LocalDate employmentEnd;
    private Long userId;


}
