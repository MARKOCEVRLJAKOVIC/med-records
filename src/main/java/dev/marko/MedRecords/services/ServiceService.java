package dev.marko.MedRecords.services;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.CreateServiceRequest;
import dev.marko.MedRecords.dtos.ServiceDto;
import dev.marko.MedRecords.dtos.UpdateServiceRequest;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.exceptions.ServiceNotFoundException;
import dev.marko.MedRecords.mappers.ServiceMapper;
import dev.marko.MedRecords.repositories.ProviderRepository;
import dev.marko.MedRecords.repositories.ServiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ServiceService {

    private final ProviderRepository providerRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final AuthService authService;

    public List<ServiceDto> findServicesForProvider(Long providerId){

        var provider = providerRepository.findById(providerId).orElseThrow(ProviderNotFoundException::new);

        var serviceList = serviceRepository.findAllByProvider(provider);
        return serviceMapper.toListDto(serviceList);

    }

    public ServiceDto findService(Long id){

        var service = serviceRepository.findById(id).orElseThrow(ServiceNotFoundException::new);
        return serviceMapper.toDto(service);

    }

    public ServiceDto createService(CreateServiceRequest request){

        var user = authService.getCurrentUser();

        var provider = providerRepository.findByIdAndUser(request.getProviderId(), user)
                .orElseThrow(ProviderNotFoundException::new);

        var service = serviceMapper.toEntity(request);
        service.setProvider(provider);

        serviceRepository.save(service);

        return serviceMapper.toDto(service);

    }

    public ServiceDto updateService(UpdateServiceRequest request, Long id){

        var service = serviceRepository.findById(id).orElseThrow(ServiceNotFoundException::new);

        serviceMapper.update(request, service);
        serviceRepository.save(service);

        return serviceMapper.toDto(service);

    }

    public void deleteService(Long id){

        var service = serviceRepository.findById(id)
                .orElseThrow(ServiceNotFoundException::new);

        serviceRepository.delete(service);

    }

}
