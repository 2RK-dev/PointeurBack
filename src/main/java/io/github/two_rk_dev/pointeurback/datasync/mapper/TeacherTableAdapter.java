package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.TeacherService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.UUID;

@Component("teacher_table_adapter")
public class TeacherTableAdapter extends AbstractEntityTableAdapter<ImportTeacherDTO> {
    private final TeacherService teacherService;

    public TeacherTableAdapter(TeacherService teacherService, ObjectMapper objectMapper, @Qualifier("mvcValidator") Validator validator) {
        super(objectMapper, validator, ImportTeacherDTO.class);
        this.teacherService = teacherService;
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(this.getMapping());
        List<@NotNull List<String>> rows = Utils.toRows(teacherService.getAll());
        return new TableData(Type.TEACHER.entityName(), headers, rows);
    }

    @Override
    protected void stage(UUID stageID, @NotNull List<ImportRow<ImportTeacherDTO>> toStage) {
        teacherService.importTeachers(toStage.stream().map(ImportRow::data));
    }
}
