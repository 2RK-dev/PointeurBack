package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.RoomDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateRoomDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomService {
    List<RoomDTO> getAll();
    RoomDTO createRoom(CreateRoomDTO dto);
    RoomDTO getRoom(Long id);
    RoomDTO updateRoom(Long id, UpdateRoomDTO dto);
    void deleteRoom(Long id);
    List<RoomDTO> getAvailableRooms(LocalDateTime start, LocalDateTime endTime, int size);
    void saveRooms(CreateRoomDTO[] rooms);
}
