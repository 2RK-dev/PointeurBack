package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.TeacherService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.UUID;

@Component("teacher_table_adapter")
public class TeacherTableAdapter extends AbstractEntityTableAdapter<ImportTeacherDTO> {
    private final TeacherService teacherService;
    private final JdbcTemplate jdbcTemplate;

    public TeacherTableAdapter(TeacherService teacherService, ObjectMapper objectMapper, @Qualifier("mvcValidator") Validator validator, JdbcTemplate jdbcTemplate) {
        super(objectMapper, validator, ImportTeacherDTO.class);
        this.teacherService = teacherService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(this.getMapping());
        List<@NotNull List<String>> rows = Utils.toRows(teacherService.getAll());
        return new TableData(Type.TEACHER.entityName(), headers, rows);
    }

    @Override
    public @NotNull Type getEntityType() {
        return Type.TEACHER;
    }

    @Override
    protected void stage(UUID stageID, @NotNull List<ImportRow<ImportTeacherDTO>> toStage) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO teacher(teacher_id, name, abbreviation) VALUES (?, ?, ?) ON CONFLICT DO NOTHING",
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
