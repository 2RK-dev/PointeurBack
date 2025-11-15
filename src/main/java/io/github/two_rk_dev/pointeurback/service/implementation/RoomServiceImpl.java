package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.RoomDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateRoomDTO;
import io.github.two_rk_dev.pointeurback.exception.InvalidDateRangeException;
import io.github.two_rk_dev.pointeurback.exception.RoomNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.RoomMapper;
import io.github.two_rk_dev.pointeurback.model.Room;
import io.github.two_rk_dev.pointeurback.repository.RoomRepository;
import io.github.two_rk_dev.pointeurback.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    public List<RoomDTO> getAll() {
        List<Room> existing = roomRepository.findAll();
        return roomMapper.toDtoList(existing);
    }

    @Override
    public RoomDTO createRoom(CreateRoomDTO dto) {
        Room newRoom = roomMapper.fromCreateDto(dto);
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
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + id));

        roomMapper.updateFromDto(dto, existingRoom);
        Room updatedRoom = roomRepository.save(existingRoom);
        return roomMapper.toDto(updatedRoom);
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public List<RoomDTO> getAvailableRooms(LocalDateTime start, @NotNull LocalDateTime endTime, int size) {
        if (endTime.isBefore(start)) {
            throw new InvalidDateRangeException("End time cannot be before startTime time");
        }
        List<Room> availableRooms = roomRepository.findAvailableRooms(start, endTime, size);
        return roomMapper.toDtoList(availableRooms);
    }

}
