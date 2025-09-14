package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class UpdateUserRequest {

    private String email;
    private String password;
    private Role role;

}
