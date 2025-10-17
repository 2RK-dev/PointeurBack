package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.CreateTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.TeacherService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("teacher_table_mapper")
public class TeacherTableMapper implements EntityTableMapper {
    private static final Map<String, CsvFieldBinding<CreateTeacherDTO>> DEFAULT_MAPPING = Utils.buildMapping(CreateTeacherDTO.class);
    private final TeacherService teacherService;
    private final ObjectMapper objectMapper;

    public TeacherTableMapper(TeacherService teacherService, ObjectMapper objectMapper) {
        this.teacherService = teacherService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void persist(@NotNull TableData tableData) {
        CreateTeacherDTO[] dtos = Utils.parseDTOs(tableData, CreateTeacherDTO.class, DEFAULT_MAPPING, objectMapper);
        teacherService.saveTeachers(dtos);
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(DEFAULT_MAPPING);
        List<@NotNull List<String>> rows = Utils.toRows(teacherService.getAll());
        return new TableData(Type.TEACHER.entityName(), headers, rows);
    }
}
