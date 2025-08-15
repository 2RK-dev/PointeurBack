package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.TeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeacherDTO;
import io.github.two_rk_dev.pointeurback.model.Teacher;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    TeacherDTO toDto(Teacher entity);

    List<TeacherDTO> toDTOList(List<Teacher> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    Teacher fromCreateDto(CreateTeacherDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateTeacherDTO dto, @MappingTarget Teacher entity);

    default Teacher createTeacherFromDto(CreateTeacherDTO dto) {
        if (dto == null) return null;
        return fromCreateDto(dto);
    }

    default void updateTeacher(UpdateTeacherDTO updateDto, Teacher teacher) {
        if (updateDto != null) {
            updateFromDto(updateDto, teacher);
        }
    }
}
