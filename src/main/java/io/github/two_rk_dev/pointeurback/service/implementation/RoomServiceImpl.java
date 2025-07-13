package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.RoomDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateRoomDTO;
import io.github.two_rk_dev.pointeurback.exception.RoomNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.RoomMapper;
import io.github.two_rk_dev.pointeurback.model.Room;
import io.github.two_rk_dev.pointeurback.repository.RoomRepository;
import io.github.two_rk_dev.pointeurback.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomMapper roomMapper;

    @Override
    public List<RoomDTO> getAll(){
        List<Room> existing = roomRepository.findAll();
        return roomMapper.toDtoList(existing);
    };

    @Override
    public RoomDTO createRoom(CreateRoomDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("CreateRoomDTO cannot be null");
        }

        Room newRoom = roomMapper.createRoomFromDto(dto);
        Room savedRoom = roomRepository.save(newRoom);
        return roomMapper.toDto(savedRoom);
    }

    @Override
    public RoomDTO getRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + id));
        return roomMapper.toDto(room);
    }

    @Override
    public RoomDTO updateRoom(Long id, UpdateRoomDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UpdateRoomDTO cannot be null");
        }

        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + id));

        roomMapper.updateRoom(dto, existingRoom);
        Room updatedRoom = roomRepository.save(existingRoom);
        return roomMapper.toDto(updatedRoom);
    }

    @Override
    public Void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + id));

        if (!room.getSchedules().isEmpty()) {
            throw new IllegalStateException("Cannot delete room with associated schedules");
        }

        roomRepository.delete(room);
        return null;
    }

    @Override
    public List<RoomDTO> getAvailableRooms(LocalDateTime start, LocalDateTime endTime, int size) {
        if (start == null || endTime == null) {
            throw new IllegalArgumentException("Start and end time must be specified");
        }
        if (endTime.isBefore(start)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Room size must be positive");
        }

        List<Room> availableRooms = roomRepository.findAvailableRooms(start, endTime, size);
        return roomMapper.toDtoList(availableRooms);
    }
}
