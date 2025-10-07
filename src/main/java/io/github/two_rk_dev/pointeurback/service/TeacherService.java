package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.CreateTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.TeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeacherDTO;

import java.util.List;

public interface TeacherService {
    List<TeacherDTO> getAll();
    TeacherDTO getTeacher(Long id);
    TeacherDTO createTeacher(CreateTeacherDTO dto);
    TeacherDTO updateTeacher(Long id, UpdateTeacherDTO dto);
    void deleteTeacher(Long id);
    void saveTeachers(CreateTeacherDTO[] teachers);
}
