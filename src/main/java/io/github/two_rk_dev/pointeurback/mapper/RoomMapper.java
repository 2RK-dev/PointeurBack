package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.RoomDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportRoomDTO;
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

    @Mapping(target = "schedules", ignore = true)
    Room fromImportDTO(ImportRoomDTO dto);
}
