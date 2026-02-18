package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.RoomDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateRoomDTO;
import io.github.two_rk_dev.pointeurback.service.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = roomService.getAll();
        return ResponseEntity.ok(rooms);
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody CreateRoomDTO dto) {
        RoomDTO createdRoom = roomService.createRoom(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{roomId}")
                .buildAndExpand(createdRoom.id())
                .toUri();
        return ResponseEntity.created(location).body(createdRoom);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable Long roomId) {
        RoomDTO room = roomService.getRoom(roomId);
        return ResponseEntity.ok(room);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Long roomId,
                                              @Valid @RequestBody UpdateRoomDTO dto) {
        RoomDTO updatedRoom = roomService.updateRoom(roomId, dto);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime,
            @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "Size must be greater than 1") int size) {

        List<RoomDTO> availableRooms = roomService.getAvailableRooms(startTime, endTime, size);
        return ResponseEntity.ok(availableRooms);
    }
}

