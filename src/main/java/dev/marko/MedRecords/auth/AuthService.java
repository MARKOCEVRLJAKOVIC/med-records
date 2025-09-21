package dev.marko.MedRecords.auth;

import dev.marko.MedRecords.dtos.RegisterAdminRequest;
import dev.marko.MedRecords.dtos.UserDto;
import dev.marko.MedRecords.entities.Role;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.UserNotFoundException;
import dev.marko.MedRecords.mappers.UserMapper;
import dev.marko.MedRecords.repositories.UserRepository;
import dev.marko.MedRecords.security.JwtConfig;
import dev.marko.MedRecords.security.JwtResponse;
import dev.marko.MedRecords.security.JwtService;
import dev.marko.MedRecords.security.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @Transactional
    public UserDto registerAdmin(RegisterAdminRequest request) {

//        var currentUser = getCurrentUser();
//
//        if(currentUser.getRole() != Role.ADMIN){
//            throw new AccessDeniedException("Only Admin can register new Admin.");
//        }

        var user = userMapper.toEntity(request);
        user.setRole(Role.ADMIN);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return userMapper.toDto(user);

    }

    public JwtResponse login(LoginRequest request, HttpServletResponse response) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFoundException::new);

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);

        return new JwtResponse(accessToken.toString());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUser();
        }

        throw new RuntimeException("Invalid authentication principal");
    }

}
