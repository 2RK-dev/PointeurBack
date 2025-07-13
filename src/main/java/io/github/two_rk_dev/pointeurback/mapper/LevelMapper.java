package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateLevelDTO;
import io.github.two_rk_dev.pointeurback.dto.LevelDTO;
import io.github.two_rk_dev.pointeurback.dto.LevelDetailsDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateLevelDTO;
import io.github.two_rk_dev.pointeurback.model.Level;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {GroupMapper.class})
public interface LevelMapper {

    // Conversion de base Level -> LevelDTO
    LevelDTO toDto(Level entity);

    // Conversion de base LevelDTO -> Level
    Level toEntity(LevelDTO dto);

    // Conversion détaillée avec les groupes
    @Mapping(target = "groups", source = "groups")
    LevelDetailsDTO toDetailsDto(Level entity);

    // Pour CreateLevelDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    Level fromCreateDto(CreateLevelDTO dto);

    // Pour UpdateLevelDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    void updateFromUpdateDto(UpdateLevelDTO dto, @MappingTarget Level entity);

    // Méthode utilitaire pour la conversion
    default void updateLevel(UpdateLevelDTO updateDto, Level level) {
        if (updateDto == null) {
            return;
        }
        updateFromUpdateDto(updateDto, level);
    }

}