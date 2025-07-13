package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.RoomDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateRoomDTO;
import io.github.two_rk_dev.pointeurback.service.implementation.RoomServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomServiceImpl roomService;

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = roomService.getAll();
        return ResponseEntity.ok(rooms);
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody CreateRoomDTO dto) {
        RoomDTO createdRoom = roomService.createRoom(dto);
        return ResponseEntity.ok(createdRoom);
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
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime endTime,
            @RequestParam(required = false, defaultValue = "1") int size) {

        List<RoomDTO> availableRooms = roomService.getAvailableRooms(start, endTime, size);
        return ResponseEntity.ok(availableRooms);
    }
}

