package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.dto.CreateTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.TeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportTeacherDTO;
import io.github.two_rk_dev.pointeurback.exception.TeacherNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.TeacherMapper;
import io.github.two_rk_dev.pointeurback.model.Teacher;
import io.github.two_rk_dev.pointeurback.repository.TeacherRepository;
import io.github.two_rk_dev.pointeurback.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    @Override
    public List<TeacherDTO> getAll() {
        List<Teacher> teachers = teacherRepository.findAll();
        return teacherMapper.toDTOList(teachers);
    }

    @Override
    public TeacherDTO getTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + id));
        return teacherMapper.toDto(teacher);
    }

    @Override
    public TeacherDTO createTeacher(CreateTeacherDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("CreateTeacherDTO cannot be null");
        }

        Teacher newTeacher = teacherMapper.createTeacherFromDto(dto);
        Teacher savedTeacher = teacherRepository.save(newTeacher);
        return teacherMapper.toDto(savedTeacher);
    }

    @Override
    public TeacherDTO updateTeacher(Long id, UpdateTeacherDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UpdateTeacherDTO cannot be null");
        }
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + id));

        teacherMapper.updateTeacher(dto, existingTeacher);
        Teacher updatedTeacher = teacherRepository.save(existingTeacher);
        return teacherMapper.toDto(updatedTeacher);
    }

    @Override
    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    @Override
    public void importTeachers(@NotNull Stream<ImportTeacherDTO> teacherDTOStream) {
        teacherRepository.saveAll(teacherDTOStream.map(teacherMapper::fromImportDTO).toList());
    }
}
