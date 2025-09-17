package dev.marko.MedRecords.controllers;

import dev.marko.MedRecords.dtos.RegisterRoomRequest;
import dev.marko.MedRecords.dtos.RoomDto;
import dev.marko.MedRecords.services.RoomService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@AllArgsConstructor
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> registerRoom(@RequestBody @Valid RegisterRoomRequest request,
                                                UriComponentsBuilder builder){

        var roomDto = roomService.registerRoom(request);
        var uri = builder.path("/rooms/{id}").buildAndExpand(roomDto.getId()).toUri();

        return ResponseEntity.created(uri).body(roomDto);

    }

}
