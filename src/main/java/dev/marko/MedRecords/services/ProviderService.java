package dev.marko.MedRecords.services;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.*;
import dev.marko.MedRecords.entities.*;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.mappers.ClientMapper;
import dev.marko.MedRecords.mappers.MedicalRecordMapper;
import dev.marko.MedRecords.mappers.ProviderMapper;
import dev.marko.MedRecords.mappers.UserMapper;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.MedicalRecordRepository;
import dev.marko.MedRecords.repositories.ProviderRepository;
import dev.marko.MedRecords.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static dev.marko.MedRecords.entities.Role.ADMIN;
import static dev.marko.MedRecords.entities.Role.PROVIDER;

@AllArgsConstructor
@Service
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    public List<ProviderDto> findAllProviders(){

        var user = authService.getCurrentUser();

        if(user.getRole() != ADMIN){
            throw new AccessDeniedException("Only Admin can see list of providers.");
        }

        var providersList = providerRepository.findAll();

        return providerMapper.toListDto(providersList);

    }

    public ProviderDto findProvider(Long id){

        var user = authService.getCurrentUser();

        var provider = getProviderByRole(id, user);

        return providerMapper.toDto(provider);

    }


    public List<ClientDto> clientsForProvider(Long providerId){
        var user = authService.getCurrentUser();

        List<Client> clientList;

        if (user.getRole() == Role.ADMIN) {
            // admin can see providers by id
            providerRepository.findById(providerId)
                    .orElseThrow(ProviderNotFoundException::new);
            clientList = clientRepository.findAllByProviderViaAppointments(providerId);
        }
        else if (user.getRole() == Role.PROVIDER) {
            // provider must be target provider
            var provider = providerRepository.findByIdAndUser(providerId, user)
                    .orElseThrow(ProviderNotFoundException::new);
            clientList = clientRepository.findAllByProviderViaAppointmentsAndUser(provider, user);
        }
        else {
            throw new AccessDeniedException("You can only view your clients.");
        }

        return clientMapper.toListDto(clientList);

    }

    @Transactional
    public ProviderDto registerProvider(RegisterProviderRequest request){

        var currentUser = authService.getCurrentUser();

        if(currentUser.getRole()!= ADMIN){
            throw new AccessDeniedException("Only Admin can add providers.");
        }

        var providerUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(PROVIDER)
                .build();

        var provider = Provider.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .specialty(request.getSpecialty())
                .licenseNumber(request.getLicenseNumber())
                .employmentStart(request.getEmploymentStart())
                .employmentEnd(request.getEmploymentEnd())
                .user(providerUser)
                .build();

        providerRepository.save(provider);

        return providerMapper.toDto(provider);

    }

    @Transactional
    public ProviderDto updateProvider(UpdateProviderRequest request, Long id){

        var currentUser = authService.getCurrentUser();

        var provider = getProviderByRole(id, currentUser);

        // update user + provider
        userMapper.updateFromProviderRequest(request, provider.getUser());
        providerMapper.update(request, provider);

        userRepository.save(provider.getUser());

        return providerMapper.toDto(provider);

    }

    @Transactional
    public void deleteProvider(Long id){

        var currentUser = authService.getCurrentUser();

        var provider = getProviderByRole(id, currentUser);

        userRepository.delete(provider.getUser());


    }

    // methods

    private Provider getProviderByRole(Long id, User user) {
        return switch (user.getRole()) {

            case ADMIN -> providerRepository.findById(id)
                    .orElseThrow(ProviderNotFoundException::new);
            case PROVIDER -> providerRepository.findByIdAndUser(id, user)
                    .orElseThrow(ProviderNotFoundException::new);

            default -> throw new AccessDeniedException("Unauthorized to view provider");

        };
    }


}
