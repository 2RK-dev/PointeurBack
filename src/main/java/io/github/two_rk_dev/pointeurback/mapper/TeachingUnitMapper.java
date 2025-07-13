package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.TeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.model.Level;
import io.github.two_rk_dev.pointeurback.model.TeachingUnit;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {LevelMapper.class})
public interface TeachingUnitMapper {

    // Conversion de base TeachingUnit -> TeachingUnitDTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "abbreviation", source = "abbreviation")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "level", source = "level")
    TeachingUnitDTO toDto(TeachingUnit entity);

    // Conversion CreateTeachingUnitDTO -> TeachingUnit
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "level", ignore = true) // Géré séparément
    @Mapping(target = "schedules", ignore = true)
    TeachingUnit fromCreateDto(CreateTeachingUnitDTO dto);

    // Mise à jour depuis UpdateTeachingUnitDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "level", ignore = true) // Géré séparément
    @Mapping(target = "schedules", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateTeachingUnitDTO dto, @MappingTarget TeachingUnit entity);

    // Méthodes utilitaires
    default TeachingUnit createTeachingUnitFromDto(CreateTeachingUnitDTO dto, Level level) {
        if (dto == null) return null;
        TeachingUnit teachingUnit = fromCreateDto(dto);
        teachingUnit.setLevel(level);
        return teachingUnit;
    }

    default void updateTeachingUnit(UpdateTeachingUnitDTO updateDto, TeachingUnit teachingUnit, Level level) {
        if (updateDto != null) {
            updateFromDto(updateDto, teachingUnit);
            teachingUnit.setLevel(level);
        }
    }
}
