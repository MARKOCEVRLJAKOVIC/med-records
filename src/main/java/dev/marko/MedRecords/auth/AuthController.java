package dev.marko.MedRecords.auth;

import dev.marko.MedRecords.dtos.RegisterAdminRequest;
import dev.marko.MedRecords.dtos.UserDto;
import dev.marko.MedRecords.security.JwtResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.AccessDeniedException;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register-admin")
    public ResponseEntity<UserDto> registerAdmin(@RequestBody RegisterAdminRequest request,
                                                 UriComponentsBuilder builder) throws AccessDeniedException {

        var userDto = authService.registerAdmin(request);
        var uri = builder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();

        return ResponseEntity.created(uri).body(userDto);

    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginClient(@RequestBody @Valid LoginRequest loginRequest,
                                                   HttpServletResponse response){

        return ResponseEntity.ok(authService.login(loginRequest, response));

    }



}
