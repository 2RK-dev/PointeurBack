package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.CreateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.TeachingUnitService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("teaching_unit_table_mapper")
public class TeachingUnitTableMapper implements EntityTableMapper {
    private static final Map<String, ColumnFieldBinding<CreateTeachingUnitDTO>> DEFAULT_MAPPING = Utils.buildMapping(CreateTeachingUnitDTO.class);
    private final TeachingUnitService teachingUnitService;
    private final ObjectMapper objectMapper;

    public TeachingUnitTableMapper(TeachingUnitService teachingUnitService, ObjectMapper objectMapper) {
        this.teachingUnitService = teachingUnitService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void persist(@NotNull TableData tableData) {
        CreateTeachingUnitDTO[] dtos = Utils.parseDTOs(tableData, CreateTeachingUnitDTO.class, DEFAULT_MAPPING, objectMapper);
        teachingUnitService.saveTeachingUnits(dtos);
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(DEFAULT_MAPPING);
        List<@NotNull List<String>> rows = Utils.toRows(teachingUnitService.getAll());
        return new TableData(Type.TEACHING_UNIT.entityName(), headers, rows);
    }
}
