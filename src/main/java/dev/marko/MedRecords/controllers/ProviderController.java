package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.*;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.ClientNotFoundException;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.exceptions.ServiceNotFoundException;
import dev.marko.MedRecords.mappers.*;
import dev.marko.MedRecords.repositories.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static dev.marko.MedRecords.entities.Role.ADMIN;
import static dev.marko.MedRecords.entities.Role.PROVIDER;

@AllArgsConstructor
@RestController
@RequestMapping("/providers")
public class ProviderController {

    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final ServiceRepository serviceRepository;

    @GetMapping
    public ResponseEntity<List<ProviderDto>> findAllProviders(){

        var user = authService.getCurrentUser();

        if(user.getRole() != ADMIN){
            throw new AccessDeniedException("Only Admin can see list of providers.");
        }

        var providersList = providerRepository.findAll();

        var providerListDto = providerMapper.toListDto(providersList);

        return ResponseEntity.ok(providerListDto);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderDto> findProvider(@PathVariable Long id){

        var user = authService.getCurrentUser();
        Provider provider = switch (user.getRole()) {

            case ADMIN -> providerRepository.findById(id)
                    .orElseThrow(ProviderNotFoundException::new);
            case PROVIDER -> providerRepository.findByIdAndUser(id, user)
                    .orElseThrow(ProviderNotFoundException::new);

            default -> throw new AccessDeniedException("Unauthorized to view provider");

        };

        return ResponseEntity.ok(providerMapper.toDto(provider));

    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<AppointmentDto>> appointmentsForProvider(@PathVariable Long id){

        var user = authService.getCurrentUser();

        var provider = providerRepository.findById(id).orElseThrow(ProviderNotFoundException::new);

        var appointmentList = switch(user.getRole()) {
            case ADMIN -> appointmentRepository.findAllByProviderId(id);
            case PROVIDER -> {
                if (!provider.getUser().getId().equals(user.getId())) {
                    throw new AccessDeniedException("You cannot access other providers' appointments.");
                }
                yield appointmentRepository.findAllByProviderId(id);
            }
            default -> throw new AccessDeniedException("Unauthorized");
        };

        var appointmentListDto = appointmentMapper.toListDto(appointmentList);

        return ResponseEntity.ok(appointmentListDto);

    }

    @GetMapping("/{id}/clients")
    public ResponseEntity<List<ClientDto>> clientsForProvider(@PathVariable Long id){
//
//        var user = authService.getCurrentUser();
//
//        var provider = providerRepository.findByIdAndUser(id, user).orElseThrow(ProviderNotFoundException::new);
//
//        var clientList = switch (user.getRole()) {
//            case ADMIN -> clientRepository.findAllByProviderId(id);
//            case PROVIDER -> {
//                if (!provider.getUser().getId().equals(user.getId())) {
//                    throw new AccessDeniedException("You cannot access other providers' clients.");
//                }
//                yield clientRepository.findAllByProviderId(id);
//            }
//            default -> throw new AccessDeniedException("");
//        };
//
//        var clientListDto = clientMapper.toListDto(clientList);

        return null;

    }


    @GetMapping("/{id}/medical-records")
    public ResponseEntity<List<MedicalRecordDto>> medicalRecordsForProvider(@PathVariable Long id){

        var user = authService.getCurrentUser();

        var medicalRecordList = switch (user.getRole()){

            case ADMIN -> medicalRecordRepository.findAllByProviderId(id);
            case PROVIDER -> {
                var provider = providerRepository.findByIdAndUser(id, user).orElseThrow(ProviderNotFoundException::new);
                if (!provider.getUser().getId().equals(user.getId())) {
                    throw new AccessDeniedException("You cannot access other providers medical records.");
                }
                yield medicalRecordRepository.findAllByProviderId(id);
            }
            default -> throw new AccessDeniedException("");

        };

        var medicalRecordListDto = medicalRecordMapper.toListDto(medicalRecordList);

        return ResponseEntity.ok(medicalRecordListDto);

    }

    @PostMapping("/{id}/medical-records")
    public ResponseEntity<?> createMedicalRecord(@RequestBody CreateMedicalRecordRequest request,
                                                 UriComponentsBuilder builder){

        var user = authService.getCurrentUser();

        var client = clientRepository.findById(request.getClientId())
                .orElseThrow(ClientNotFoundException::new);

        var service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(ServiceNotFoundException::new);

        var provider = providerRepository.findByIdAndUser(request.getProviderId(), user)
                .orElseThrow(ProviderNotFoundException::new);



        var medicalRecord = medicalRecordMapper.toEntity(request);


        medicalRecord.setClient(client);
        medicalRecord.setService(service);
        medicalRecord.setProvider(provider);


        medicalRecordRepository.save(medicalRecord);

        var medicalRecordDto = medicalRecordMapper.toDto(medicalRecord);

        var uri = builder.path("/medical-records/{id}").buildAndExpand(medicalRecordDto.getId()).toUri();

        return ResponseEntity.created(uri).body(medicalRecordDto);

    }

    @PostMapping
    public ResponseEntity<ProviderDto> registerProvider(@RequestBody RegisterProviderRequest request,
                                                        UriComponentsBuilder builder){

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

        var providerDto = providerMapper.toDto(provider);

        var uri = builder.path("/providers/{id}").buildAndExpand(providerDto.getId()).toUri();

        return ResponseEntity.created(uri).body(providerDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProviderDto> updateProvider(@RequestBody @Valid UpdateProviderRequest request,
                                                      @PathVariable Long id) {
        var currentUser = authService.getCurrentUser();


        var provider = switch (currentUser.getRole()){
            case PROVIDER -> providerRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(dev.marko.MedRecords.exceptions.ProviderNotFoundException::new);
            case ADMIN -> providerRepository.findById(id)
                    .orElseThrow(ProviderNotFoundException::new);
            default -> throw new AccessDeniedException("Only providers or admins can update a provider.");
        };


        // update user + provider
        userMapper.updateFromProviderRequest(request, provider.getUser());
        providerMapper.update(request, provider);

        userRepository.save(provider.getUser());

        var providerDto = providerMapper.toDto(provider);
        return ResponseEntity.ok(providerDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id){

        var currentUser = authService.getCurrentUser();

        var provider = switch (currentUser.getRole()){
            case PROVIDER -> providerRepository.findByIdAndUser(id, currentUser)
                    .orElseThrow(dev.marko.MedRecords.exceptions.ProviderNotFoundException::new);
            case ADMIN -> providerRepository.findById(id)
                    .orElseThrow(ProviderNotFoundException::new);
            default -> throw new AccessDeniedException("You can only delete your profile.");
        };

        userRepository.delete(provider.getUser());

        return ResponseEntity.noContent().build();

    }



}
