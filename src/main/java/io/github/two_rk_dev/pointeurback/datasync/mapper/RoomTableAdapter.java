package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.RoomService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.UUID;

@Component("room_table_adapter")
public class RoomTableAdapter extends AbstractEntityTableAdapter<ImportRoomDTO> {
    private final RoomService roomService;

    public RoomTableAdapter(RoomService roomService, ObjectMapper objectMapper, @Qualifier("mvcValidator") Validator validator) {
        super(objectMapper, validator, ImportRoomDTO.class);
        this.roomService = roomService;
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(this.getMapping());
        List<@NotNull List<String>> rows = Utils.toRows(roomService.getAll());
        return new TableData(Type.ROOM.entityName(), headers, rows);
    }

    @Override
    protected void stage(UUID stageID, @NotNull List<ImportRow<ImportRoomDTO>> toStage) {
        roomService.importRooms(toStage.stream().map(ImportRow::data));
    }
}
