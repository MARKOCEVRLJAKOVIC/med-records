package dev.marko.MedRecords.services;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.CreateMedicalRecordRequest;
import dev.marko.MedRecords.dtos.MedicalRecordDto;
import dev.marko.MedRecords.dtos.UpdateMedicalRecordRequest;
import dev.marko.MedRecords.entities.MedicalRecord;
import dev.marko.MedRecords.entities.Role;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.ClientNotFoundException;
import dev.marko.MedRecords.exceptions.MedicalRecordNotFoundException;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.exceptions.ServiceNotFoundException;
import dev.marko.MedRecords.mappers.MedicalRecordMapper;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.MedicalRecordRepository;
import dev.marko.MedRecords.repositories.ProviderRepository;
import dev.marko.MedRecords.repositories.ServiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class MedicalRecordService {

    private final AuthService authService;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final ClientRepository clientRepository;
    private final ProviderRepository providerRepository;
    private final ServiceRepository serviceRepository;

    public List<MedicalRecordDto> findMedicalRecordsForClient(Long clientId){

        var user = authService.getCurrentUser();

        var client = clientRepository.findByIdAndUser(clientId, user)
                .orElseThrow(ClientNotFoundException::new);

        var medicalRecordList = medicalRecordRepository.findAllByClient(client);

        return medicalRecordMapper.toListDto(medicalRecordList);

    }

    public List<MedicalRecordDto> findMedicalRecordsForProvider(Long providerId){

        var user = authService.getCurrentUser();

        var provider = switch (user.getRole()) {
            case ADMIN -> providerRepository.findById(providerId).orElseThrow(ProviderNotFoundException::new);
            case PROVIDER -> providerRepository.findByIdAndUser(providerId, user)
                    .orElseThrow(ProviderNotFoundException::new);
            default -> throw new AccessDeniedException("Only provider see his/her medical records.");
        };

        var medicalRecordList = medicalRecordRepository.findAllByProvider(provider);

        return medicalRecordMapper.toListDto(medicalRecordList);

    }

    public MedicalRecordDto findMedicalRecord(Long id){

        var user = authService.getCurrentUser();

        var medicalRecord = getMedicalRecordByRole(id, user);

        return medicalRecordMapper.toDto(medicalRecord);

    }

    @Transactional
    public MedicalRecordDto createMedicalRecord(CreateMedicalRecordRequest request){

        var user = authService.getCurrentUser();

        var client = clientRepository.findById(request.getClientId())
                .orElseThrow(ClientNotFoundException::new);

        var service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(ServiceNotFoundException::new);

        var provider = providerRepository.findByIdAndUser(request.getProviderId(), user)
                .orElseThrow(ProviderNotFoundException::new);

        if (!service.getProvider().getId().equals(provider.getId())) {
            throw new IllegalArgumentException("Selected service does not belong to the given provider.");
        }

        if (user.getRole() == Role.CLIENT && !client.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Clients can only create records for themselves.");
        }



        var medicalRecord = medicalRecordMapper.toEntity(request);

        medicalRecord.setClient(client);
        medicalRecord.setService(service);
        medicalRecord.setProvider(provider);

        medicalRecordRepository.save(medicalRecord);

        return medicalRecordMapper.toDto(medicalRecord);

    }

    @Transactional
    public MedicalRecordDto updateMedicalRecord(UpdateMedicalRecordRequest request, Long id){

        var user = authService.getCurrentUser();

        var medicalRecord = getMedicalRecordByRole(id, user);

        if (request.getClientId() != null) {
            var client = clientRepository.findById(request.getClientId())
                    .orElseThrow(ClientNotFoundException::new);

            // if user = client check if he is changing his client
            if (user.getRole() == Role.CLIENT && !client.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("Clients can only update their own records.");
            }
        }

        medicalRecordMapper.update(request, medicalRecord);
        medicalRecordRepository.save(medicalRecord);

        return medicalRecordMapper.toDto(medicalRecord);

    }

    @Transactional
    public void deleteMedicalRecord(Long id){

        var user = authService.getCurrentUser();
        var medicalRecord = switch (user.getRole()){
            case ADMIN -> medicalRecordRepository.findById(id)
                    .orElseThrow(MedicalRecordNotFoundException::new);
            case PROVIDER -> medicalRecordRepository.findByIdAndProviderUser(id, user)
                    .orElseThrow(MedicalRecordNotFoundException::new);
            default -> throw new AccessDeniedException("Access denied");
        };

        medicalRecordRepository.delete(medicalRecord);

    }

    private MedicalRecord getMedicalRecordByRole(Long id, User user) {
        return switch (user.getRole()) {
            case CLIENT -> medicalRecordRepository.findByIdAndClientUser(id, user)
                    .orElseThrow(MedicalRecordNotFoundException::new);
            case PROVIDER -> medicalRecordRepository.findByIdAndProviderUser(id, user)
                    .orElseThrow(MedicalRecordNotFoundException::new);
            case ADMIN -> medicalRecordRepository.findById(id)
                    .orElseThrow(MedicalRecordNotFoundException::new);
            default -> throw new AccessDeniedException("Not authorized");
        };
    }



}
