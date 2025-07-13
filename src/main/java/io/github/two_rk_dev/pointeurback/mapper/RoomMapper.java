package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.RoomDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateRoomDTO;
import io.github.two_rk_dev.pointeurback.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    // Conversion de base Room -> RoomDTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "abbreviation", source = "abbreviation")
    @Mapping(target = "size", source = "size")
    RoomDTO toDto(Room entity);

    // Conversion CreateRoomDTO -> Room
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    Room fromCreateDto(CreateRoomDTO dto);

    // Mise à jour depuis UpdateRoomDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    void updateFromDto(UpdateRoomDTO dto, @MappingTarget Room entity);

    // Méthode utilitaire pour la création
    default Room createRoomFromDto(CreateRoomDTO dto) {
        if (dto == null) {
            return null;
        }
        return fromCreateDto(dto);
    }

    // Méthode utilitaire pour la mise à jour
    default void updateRoom(UpdateRoomDTO updateDto, Room room) {
        if (updateDto != null) {
            updateFromDto(updateDto, room);
        }
    }
}
