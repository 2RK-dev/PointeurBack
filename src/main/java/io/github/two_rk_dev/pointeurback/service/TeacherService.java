package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.CreateTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.TeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportTeacherDTO;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public interface TeacherService {
    List<TeacherDTO> getAll();

    TeacherDTO getTeacher(Long id);

    TeacherDTO createTeacher(CreateTeacherDTO dto);

    TeacherDTO updateTeacher(Long id, UpdateTeacherDTO dto);

    void deleteTeacher(Long id);

    void importTeachers(@NotNull Stream<ImportTeacherDTO> teacherDTOStream);
}
