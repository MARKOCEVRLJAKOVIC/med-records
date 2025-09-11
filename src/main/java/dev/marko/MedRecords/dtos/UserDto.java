package dev.marko.MedRecords.dtos;

import dev.marko.MedRecords.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private Role role;

}
