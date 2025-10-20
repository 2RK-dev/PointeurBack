package io.github.two_rk_dev.pointeurback.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.two_rk_dev.pointeurback.dto.CreateLevelDTO;
import io.github.two_rk_dev.pointeurback.dto.LevelDTO;
import io.github.two_rk_dev.pointeurback.dto.LevelDetailsDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateLevelDTO;
import io.github.two_rk_dev.pointeurback.model.Level;

@Mapper(componentModel = "spring")
public interface LevelMapper {

  LevelDTO toDto(Level entity);

  @MappingQualifier.LevelToDtoWithoutGroups
  default LevelDTO toDtoWithoutGroups(Level entity) {
    if (entity == null)
      return null;
    return new LevelDTO(entity.getId(), entity.getName(), entity.getAbbreviation());
  }

  @Mapping(target = "level", source = ".")
  @Mapping(target = "groups", source = "groups")
  LevelDetailsDTO toDetailsDto(Level entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "groups", ignore = true)
  Level fromCreateDto(CreateLevelDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "groups", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateFromUpdateDto(UpdateLevelDTO dto, @MappingTarget Level entity);

  List<LevelDTO> toDtoList(List<Level> entities);

  default void updateLevel(UpdateLevelDTO updateDto, Level level) {
    if (updateDto == null) {
      return;
    }
    updateFromUpdateDto(updateDto, level);
  }

}
