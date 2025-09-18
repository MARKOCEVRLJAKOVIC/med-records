package dev.marko.MedRecords.services;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.RegisterRoomRequest;
import dev.marko.MedRecords.dtos.RoomDto;
import dev.marko.MedRecords.dtos.UpdateRoomRequest;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.Room;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.exceptions.RoomNotFoundException;
import dev.marko.MedRecords.exceptions.UserNotFoundException;
import dev.marko.MedRecords.mappers.RoomMapper;
import dev.marko.MedRecords.repositories.ProviderRepository;
import dev.marko.MedRecords.repositories.RoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class RoomService {

    public final RoomMapper roomMapper;
    public final RoomRepository roomRepository;
    public final ProviderRepository providerRepository;
    private final AuthService authService;

    public List<RoomDto> findAllRoomsForProvider(Long providerId){

        var user = authService.getCurrentUser();

        var provider = getProviderForRole(providerId, user);

        var roomList = roomRepository.findAllByProvider(provider);

        return roomMapper.toListDto(roomList);

    }

    public RoomDto findRoom(Long id){

        var user = authService.getCurrentUser();
        var room = getRoomForRole(id, user);

        return roomMapper.toDto(room);

    }

    @Transactional
    public RoomDto registerRoom(RegisterRoomRequest request){

        var user = authService.getCurrentUser();

        var provider = getProviderForRole(request.getProviderId(), user);

        var room = roomMapper.toEntity(request);
        room.setProvider(provider);

        roomRepository.save(room);

        return roomMapper.toDto(room);

    }

    @Transactional
    public RoomDto updateRoom(Long id, UpdateRoomRequest request){

        var user = authService.getCurrentUser();

        var room = getRoomForRole(id, user);
        roomMapper.update(request, room);
        roomRepository.save(room);

        return roomMapper.toDto(room);

    }

    @Transactional
    public void deleteRoom(Long id) {

        var user = authService.getCurrentUser();
        var room = getRoomForRole(id, user);
        roomRepository.delete(room);

    }


    // methods

    private Provider getProviderForRole(Long providerId, User user) {
        return switch (user.getRole()) {
            case ADMIN -> providerRepository.findById(providerId)
                    .orElseThrow(ProviderNotFoundException::new);
            case PROVIDER -> providerRepository.findByIdAndUser(providerId, user)
                    .orElseThrow(ProviderNotFoundException::new);
            default -> throw new AccessDeniedException("Access denied.");
        };
    }

    private Room getRoomForRole(Long id, User user) {
        return switch (user.getRole()) {

            case ADMIN -> roomRepository.findById(id)
                    .orElseThrow(RoomNotFoundException::new);
            case PROVIDER -> roomRepository.findByIdAndProviderUser(id, user)
                    .orElseThrow(UserNotFoundException::new);
            default -> throw new AccessDeniedException("Access denied");

        };
    }


}
