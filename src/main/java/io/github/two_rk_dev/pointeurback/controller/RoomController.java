package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody CreateRoomDTO dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable Long roomId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Long roomId, @RequestBody UpdateRoomDTO dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

