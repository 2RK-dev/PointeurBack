package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateGroupDTO;
import io.github.two_rk_dev.pointeurback.dto.GroupDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateGroupDTO;
import io.github.two_rk_dev.pointeurback.model.Group;
import io.github.two_rk_dev.pointeurback.model.Level;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {LevelMapper.class})
public interface GroupMapper {

    // Conversion de base Group -> GroupDTO
    @Mapping(target = "level", source = "level")
    GroupDTO toDto(Group entity);

    // Conversion de base GroupDTO -> Group
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "level.groups", ignore = true) // Évite la référence circulaire
    Group toEntity(GroupDTO dto);

    // Pour CreateGroupDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "level", ignore = true) // À définir séparément
    @Mapping(target = "schedules", ignore = true)
    Group fromCreateDto(CreateGroupDTO dto);

    // Méthode complète avec niveau optionnel
    default Group fromCreateDto(CreateGroupDTO dto, Level level) {
        Group group = fromCreateDto(dto);
        group.setLevel(level);
        return group;
    }

    // Pour UpdateGroupDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    void updateFromUpdateDto(UpdateGroupDTO dto, @MappingTarget Group entity);

    // Méthode utilitaire pour la conversion
    default void updateGroup(UpdateGroupDTO updateDto, Group group) {
        if (updateDto == null) {
            return;
        }
        updateFromUpdateDto(updateDto, group);
    }

}
