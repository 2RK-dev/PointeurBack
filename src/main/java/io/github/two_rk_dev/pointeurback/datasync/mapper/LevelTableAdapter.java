package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportLevelDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.LevelService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.UUID;

@Component("level_table_adapter")
public class LevelTableAdapter extends AbstractEntityTableAdapter<ImportLevelDTO> {
    private final LevelService levelService;
    private final JdbcTemplate jdbcTemplate;

    public LevelTableAdapter(LevelService levelService, ObjectMapper objectMapper, @Qualifier("mvcValidator") Validator validator, JdbcTemplate jdbcTemplate) {
        super(objectMapper, validator, ImportLevelDTO.class);
        this.levelService = levelService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(this.getMapping());
        List<@NotNull List<String>> rows = Utils.toRows(levelService.getAll());
        return new TableData(Type.LEVEL.entityName(), headers, rows);
    }

    @Override
    public @NotNull Type getEntityType() {
        return Type.LEVEL;
    }

    @Override
    protected void stage(UUID stageID, @NotNull List<ImportRow<ImportLevelDTO>> toStage, boolean ignoreConflicts) {
        String sql = ignoreConflicts ?
                "INSERT INTO level(level_id, name, abbreviation) VALUES (?, ?, ?) ON CONFLICT DO NOTHING" :
                "INSERT INTO level(level_id, name, abbreviation) VALUES (?, ?, ?) ON CONFLICT(level_id) DO UPDATE SET name = excluded.name, abbreviation = excluded.abbreviation";
        jdbcTemplate.batchUpdate(
                sql,
                toStage,
                100,
                (ps, argument) -> {
                    ps.setLong(1, argument.data().id());
                    ps.setString(2, argument.data().name());
                    ps.setString(3, argument.data().abbreviation());
                }
        );
    }
}
