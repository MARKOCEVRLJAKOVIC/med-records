package dev.marko.MedRecords.services;

import dev.marko.MedRecords.dtos.UpdateUserRequest;
import dev.marko.MedRecords.dtos.UserDto;
import dev.marko.MedRecords.entities.Role;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.UserNotFoundException;
import dev.marko.MedRecords.mappers.UserMapper;
import dev.marko.MedRecords.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public List<UserDto> getClientUsers(){

        var usersList = userRepository.findAllByRole(Role.CLIENT);

        return userMapper.toListDto(usersList);

    }

    public List<UserDto> getProviderUsers(){

        var usersList = userRepository.findAllByRole(Role.PROVIDER);

        return userMapper.toListDto(usersList);

    }

    public UserDto getUser(Long id){

        var user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(user);

    }

    public UserDto updateUser(UpdateUserRequest request, Long id){
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if(user.getRole() == Role.ADMIN){
            throw new AccessDeniedException("");
        }

        userMapper.update(request, user);
        userRepository.save( user);

        return userMapper.toDto(user);
    }

    public void deleteUser(Long id){

        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if(user.getRole() == Role.ADMIN){
            throw new AccessDeniedException("You can't delete Admin users.");
        }

        userRepository.delete(user);

    }

}
