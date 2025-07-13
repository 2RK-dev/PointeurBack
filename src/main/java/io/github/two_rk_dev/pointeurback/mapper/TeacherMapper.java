package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.TeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeacherDTO;
import io.github.two_rk_dev.pointeurback.model.Teacher;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    // Conversion de base Teacher -> TeacherDTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "abbreviation", source = "abbreviation")
    TeacherDTO toDto(Teacher entity);

    // Conversion CreateTeacherDTO -> Teacher
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    Teacher fromCreateDto(CreateTeacherDTO dto);

    // Mise à jour depuis UpdateTeacherDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateTeacherDTO dto, @MappingTarget Teacher entity);

    // Méthodes utilitaires
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
