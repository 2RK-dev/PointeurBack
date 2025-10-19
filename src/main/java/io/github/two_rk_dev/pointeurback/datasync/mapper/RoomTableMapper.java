package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.RoomService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("room_table_mapper")
public class RoomTableMapper implements EntityTableMapper {
    private static final Map<String, ColumnFieldBinding<CreateRoomDTO>> DEFAULT_MAPPING = Utils.buildMapping(CreateRoomDTO.class);
    private final RoomService roomService;
    private final ObjectMapper objectMapper;

    public RoomTableMapper(RoomService roomService, ObjectMapper objectMapper) {
        this.roomService = roomService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void persist(@NotNull TableData tableData) {
        CreateRoomDTO[] dtos = Utils.parseDTOs(tableData, CreateRoomDTO.class, DEFAULT_MAPPING, objectMapper);
        roomService.saveRooms(dtos);
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(DEFAULT_MAPPING);
        List<@NotNull List<String>> rows = Utils.toRows(roomService.getAll());
        return new TableData(Type.ROOM.entityName(), headers, rows);
    }
}
