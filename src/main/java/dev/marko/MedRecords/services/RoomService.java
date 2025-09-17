package dev.marko.MedRecords.services;

import dev.marko.MedRecords.auth.AuthService;
import dev.marko.MedRecords.dtos.RegisterRoomRequest;
import dev.marko.MedRecords.dtos.RoomDto;
import dev.marko.MedRecords.entities.Provider;
import dev.marko.MedRecords.entities.User;
import dev.marko.MedRecords.exceptions.ProviderNotFoundException;
import dev.marko.MedRecords.mappers.RoomMapper;
import dev.marko.MedRecords.repositories.ProviderRepository;
import dev.marko.MedRecords.repositories.RoomRepository;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class RoomService {

    public final RoomMapper roomMapper;
    public final RoomRepository roomRepository;
    public final ProviderRepository providerRepository;
    private final AuthService authService;

    @Transactional
    public RoomDto registerRoom(RegisterRoomRequest request){

        var user = authService.getCurrentUser();

        var provider = findProviderByRole(request, user);

        var room = roomMapper.toEntity(request);
        room.setProvider(provider);

        roomRepository.save(room);

        return roomMapper.toDto(room);

    }



    // methods

    private Provider findProviderByRole(RegisterRoomRequest request, User user) {
        return switch (user.getRole()) {
            case ADMIN -> providerRepository.findById(request.getProviderId())
                    .orElseThrow(ProviderNotFoundException::new);
            case PROVIDER -> providerRepository.findByIdAndUser(request.getProviderId(), user)
                    .orElseThrow(ProviderNotFoundException::new);
            default -> throw new AccessDeniedException("Access denied.");
        };
    }


}
