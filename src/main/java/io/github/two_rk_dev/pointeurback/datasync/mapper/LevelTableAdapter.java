package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.CreateLevelDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.LevelService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("level_table_adapter")
public class LevelTableAdapter implements EntityTableAdapter {
    private static final Map<String, ColumnFieldBinding<CreateLevelDTO>> DEFAULT_MAPPING = Utils.buildMapping(CreateLevelDTO.class);
    private final LevelService levelService;
    private final ObjectMapper objectMapper;

    public LevelTableAdapter(LevelService levelService, ObjectMapper objectMapper) {
        this.levelService = levelService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void persist(@NotNull TableData tableData) {
        CreateLevelDTO[] dtos = Utils.parseDTOs(tableData, CreateLevelDTO.class, DEFAULT_MAPPING, objectMapper).toArray(CreateLevelDTO[]::new);
        for (CreateLevelDTO dto : dtos) {
            levelService.createLevel(dto);
        }
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(DEFAULT_MAPPING);
        List<@NotNull List<String>> rows = Utils.toRows(levelService.getAll());
        return new TableData(Type.LEVEL.entityName(), headers, rows);
    }
}
