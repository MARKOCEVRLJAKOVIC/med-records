package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.Role;
import lombok.Data;

@Data
public class RegisterAdminRequest {

    private String email;
    private String password;


}
