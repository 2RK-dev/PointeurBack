package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportRoomDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.RoomService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.UUID;

@Component("room_table_adapter")
public class RoomTableAdapter extends AbstractEntityTableAdapter<ImportRoomDTO> {
    private final RoomService roomService;
    private final JdbcTemplate jdbcTemplate;

    public RoomTableAdapter(RoomService roomService, ObjectMapper objectMapper, @Qualifier("mvcValidator") Validator validator, JdbcTemplate jdbcTemplate) {
        super(objectMapper, validator, ImportRoomDTO.class);
        this.roomService = roomService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(this.getMapping());
        List<@NotNull List<String>> rows = Utils.toRows(roomService.getAll());
        return new TableData(Type.ROOM.entityName(), headers, rows);
    }

    @Override
    public @NotNull Type getEntityType() {
        return Type.ROOM;
    }

    @Override
    protected void stage(UUID stageID, @NotNull List<ImportRow<ImportRoomDTO>> toStage) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO room(room_id, name, size, abbreviation) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING",
                toStage,
                100,
                (ps, argument) -> {
                    ps.setLong(1, argument.data().id());
                    ps.setString(2, argument.data().name());
                    ps.setInt(3, argument.data().size());
                    ps.setString(4, argument.data().abbreviation());
                }
        );
    }
}
