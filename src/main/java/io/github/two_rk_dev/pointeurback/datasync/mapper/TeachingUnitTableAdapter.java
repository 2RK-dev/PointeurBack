package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.SyncError;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.TeachingUnitService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.UUID;

@Component("teaching_unit_table_adapter")
public class TeachingUnitTableAdapter extends AbstractEntityTableAdapter<ImportTeachingUnitDTO> {
    private final TeachingUnitService teachingUnitService;
    private final JdbcTemplate jdbcTemplate;

    public TeachingUnitTableAdapter(TeachingUnitService teachingUnitService, ObjectMapper objectMapper, @Qualifier("mvcValidator") Validator validator, JdbcTemplate jdbcTemplate) {
        super(objectMapper, validator, ImportTeachingUnitDTO.class);
        this.teachingUnitService = teachingUnitService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(this.getMapping());
        List<@NotNull List<String>> rows = Utils.toRows(teachingUnitService.getAll());
        return new TableData(Type.TEACHING_UNIT.entityName(), headers, rows);
    }

    @Override
    protected void stage(UUID stageID, @NotNull List<ImportRow<ImportTeachingUnitDTO>> toStage) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO staging_teaching_unit(teaching_unit_id, abbreviation, name, level_id, stage_id, filename, row_index, row_context) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                toStage,
                100,
                (ps, argument) -> {
                    ImportTeachingUnitDTO teachingUnit = argument.data();
                    ps.setLong(1, teachingUnit.id());
                    ps.setString(2, teachingUnit.abbreviation());
                    ps.setString(3, teachingUnit.name());
                    ps.setLong(4, teachingUnit.levelId());
                    ps.setString(5, stageID.toString());
                    ps.setString(6, argument.filename());
                    ps.setInt(7, argument.rowIndex());
                    ps.setString(8, argument.rowContext());
                }
        );
    }

    /**
     * Promote the valid staged data, return the invalid ones, and clean up the staging table.
     * @param stageID unique identifier for the import session to finalize
     * @return a list of the invalid data's information
     */
    @Override
    public List<SyncError> finalize(UUID stageID) {
        String insertValid = """
                INSERT INTO teaching_unit(teaching_unit_id, level_id, name, abbreviation)
                SELECT s.teaching_unit_id, s.level_id, s.name, s.abbreviation
                FROM staging_teaching_unit s
                WHERE s.stage_id = ? AND EXISTS (SELECT 1 FROM level l WHERE l.level_id = s.level_id)
                """;
        jdbcTemplate.update(insertValid, stageID);
        String selectInvalid = """
                SELECT s.filename, s.row_index, s.row_context
                FROM staging_teaching_unit s
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
        jdbcTemplate.update("DELETE FROM staging_teaching_unit WHERE stage_id = ?", stageID);
        return errors;
    }
}
