package dev.marko.MedRecords.services;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.auth.RegisterClientRequest;
import dev.marko.MedRecords.dtos.ClientDto;
import dev.marko.MedRecords.dtos.UpdateClientRequest;
import dev.marko.MedRecords.entities.Client;
import dev.marko.MedRecords.entities.Role;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.ClientNotFoundException;
import dev.marko.MedRecords.mappers.ClientMapper;
import dev.marko.MedRecords.repositories.ClientRepository;
import dev.marko.MedRecords.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class ClientService {

    private final AuthService authService;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public List<ClientDto> findClients(){

        var user = authService.getCurrentUser();
        var clientList = clientRepository.findAllByUser(user);

        return clientMapper.toListDto(clientList);

    }

    public ClientDto findClient(Long id){

        var user = authService.getCurrentUser();
        var client = clientRepository.findByIdAndUser(id, user).orElseThrow(ClientNotFoundException::new);

        return clientMapper.toDto(client);

    }

    @Transactional
    public ClientDto registerClient(RegisterClientRequest request){

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CLIENT)
                .build();

        var client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .allergies(request.getAllergies())
                .medicalNotes(request.getMedicalNotes())
                .user(user)
                .build();

        user.setClient(client);
        userRepository.save(user);
        return clientMapper.toDto(client);

    }

    @Transactional
    public ClientDto updateClient(UpdateClientRequest request, Long id){

        var user = authService.getCurrentUser();

        var client = clientRepository.findByIdAndUser(id, user).orElseThrow(ClientNotFoundException::new);

        clientMapper.update(request, client);

        clientRepository.save(client);

        return clientMapper.toDto(client);

    }

    @Transactional
    public void deleteClient(Long id){

        var user = authService.getCurrentUser();

        var client = clientRepository.findByIdAndUser(id, user)
                .orElseThrow(ClientNotFoundException::new);

        userRepository.delete(client.getUser());

    }

}
