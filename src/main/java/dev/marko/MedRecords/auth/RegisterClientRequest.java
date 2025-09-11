package dev.marko.MedRecords.auth;

import dev.marko.MedRecords.entities.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterClientRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String allergies;
    private String medicalNotes;



}
