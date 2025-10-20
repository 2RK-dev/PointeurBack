package io.github.two_rk_dev.pointeurback.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.two_rk_dev.pointeurback.dto.CreateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.TeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.model.Level;
import io.github.two_rk_dev.pointeurback.model.TeachingUnit;

@Mapper(componentModel = "spring", uses = { LevelMapper.class })
public interface TeachingUnitMapper {

  @Mapping(target = "level", source = "level")
  TeachingUnitDTO toDto(TeachingUnit entity);

  List<TeachingUnitDTO> toDto(List<TeachingUnit> entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "level", ignore = true)
  @Mapping(target = "schedules", ignore = true)
  TeachingUnit fromCreateDto(CreateTeachingUnitDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "level", ignore = true)
  @Mapping(target = "schedules", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateFromDto(UpdateTeachingUnitDTO dto, @MappingTarget TeachingUnit entity);

  default TeachingUnit createTeachingUnitFromDto(CreateTeachingUnitDTO dto, Level level) {
    if (dto == null)
      return null;
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
