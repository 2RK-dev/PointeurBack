package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportGroupDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.SyncError;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.GroupService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.UUID;

@Component("group_table_adapter")
public class GroupTableAdapter extends AbstractEntityTableAdapter<ImportGroupDTO> {
    private final GroupService groupService;
    private final JdbcTemplate jdbcTemplate;

    public GroupTableAdapter(GroupService groupService, ObjectMapper objectMapper, @Qualifier("mvcValidator") Validator validator, JdbcTemplate jdbcTemplate) {
        super(objectMapper, validator, ImportGroupDTO.class);
        this.groupService = groupService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(this.getMapping());
        List<@NotNull List<String>> rows = Utils.toRows(groupService.getAll());
        return new TableData(Type.GROUP.entityName(), headers, rows);
    }

    /**
     * Promote the valid staged data, return the invalid ones, and clean up the staging table.
     * @param stageID unique identifier for the import session to finalize
     * @return a list of the invalid data's information
     */
    @Override
    public List<SyncError> finalize(UUID stageID) {
        String insertValid = """
                    INSERT INTO groups (group_id, level_id, name, size)
                    SELECT s.group_id, s.level_id, s.name, s.size
                    FROM staging_groups s
                    WHERE s.stage_id = ? AND EXISTS (SELECT 1 FROM level l WHERE l.level_id = s.level_id)
                """;
        jdbcTemplate.update(insertValid, stageID);
        String selectInvalid = """
                    SELECT s.filename, s.row_index, s.row_context
                    FROM staging_groups s
                    WHERE s.stage_id = ? AND NOT EXISTS (SELECT 1 FROM level l WHERE l.level_id = s.level_id)
                """;
        List<SyncError> errors = jdbcTemplate.query(
                selectInvalid,
                (rs, rowNum) -> SyncError.forEntity(
                        rs.getString("filename"),
                        rs.getInt("row_index"),
                        "Invalid level_id (no matching level found)",
                        rs.getString("row_context")
                ),
                stageID
        );
        jdbcTemplate.update("DELETE FROM staging_groups WHERE stage_id = ?", stageID);
        return errors;
    }

    @Override
    protected void stage(UUID stageID, @NotNull List<ImportRow<ImportGroupDTO>> toStage) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO staging_groups (group_id, level_id, name, size, stage_id, filename, row_index, row_context) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                toStage,
                100,
                (ps, argument) -> {
                    ImportGroupDTO group = argument.data();
                    ps.setLong(1, group.id());
                    ps.setLong(2, group.levelId());
                    ps.setString(3, group.name());
                    ps.setInt(4, group.size());
                    ps.setString(5, stageID.toString());
                    ps.setString(6, argument.filename());
                    ps.setInt(7, argument.rowIndex());
                    ps.setString(8, argument.rowContext());
                }
        );
    }
}
