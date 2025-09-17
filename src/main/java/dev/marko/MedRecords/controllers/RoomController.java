package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.RegisterRoomRequest;
import dev.marko.MedRecords.dtos.RoomDto;
import dev.marko.MedRecords.dtos.UpdateRoomRequest;
import dev.marko.MedRecords.services.RoomService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/providerId/provider")
    public ResponseEntity<List<RoomDto>> findAllRoomsForProvider(@PathVariable Long providerId){

        var roomListDto = roomService.findAllRoomsForProvider(providerId);
        return ResponseEntity.ok(roomListDto);

    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> findRoom(@PathVariable Long id){

        var roomDto = roomService.findRoom(id);
        return ResponseEntity.ok(roomDto);

    }

    @PostMapping
    public ResponseEntity<RoomDto> registerRoom(@RequestBody @Valid RegisterRoomRequest request,
                                                UriComponentsBuilder builder){

        var roomDto = roomService.registerRoom(request);
        var uri = builder.path("/rooms/{id}").buildAndExpand(roomDto.getId()).toUri();

        return ResponseEntity.created(uri).body(roomDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id,
                                              @RequestBody @Valid UpdateRoomRequest request){

        var roomDto = roomService.updateRoom(id, request);
        return ResponseEntity.ok(roomDto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id){

        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();

    }

}
