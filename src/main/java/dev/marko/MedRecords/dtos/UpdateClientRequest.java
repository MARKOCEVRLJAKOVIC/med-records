package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateClientRequest {

    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String allergies;
    private String medicalNotes;


}
