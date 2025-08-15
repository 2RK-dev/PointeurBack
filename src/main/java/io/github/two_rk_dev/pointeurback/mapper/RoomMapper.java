package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.RoomDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateRoomDTO;
import io.github.two_rk_dev.pointeurback.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomDTO toDto(Room entity);

    List<RoomDTO> toDtoList(List<Room> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    Room fromCreateDto(CreateRoomDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    void updateFromDto(UpdateRoomDTO dto, @MappingTarget Room entity);

    default Room createRoomFromDto(CreateRoomDTO dto) {
        if (dto == null) {
            return null;
        }
        return fromCreateDto(dto);
    }

    default void updateRoom(UpdateRoomDTO updateDto, Room room) {
        if (updateDto != null) {
            updateFromDto(updateDto, room);
        }
    }
}
