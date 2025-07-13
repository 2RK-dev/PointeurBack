package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateGroupDTO;
import io.github.two_rk_dev.pointeurback.dto.GroupDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateGroupDTO;
import io.github.two_rk_dev.pointeurback.model.Group;
import io.github.two_rk_dev.pointeurback.model.Level;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LevelMapper.class})
public interface GroupMapper {

    // Conversion de base Group -> GroupDTO
    @Named("toDto")
    @Mapping(target = "level", qualifiedBy = MappingQualifier.LevelToDtoWithoutGroups.class)
    GroupDTO toDto(Group entity);

    // For avoiding circular references when used by LevelMapper
    // Méthode alternative sans le niveau
    @Named("toDtoWithoutLevel")
    @MappingQualifier.GroupToDtoWithoutLevel
    @Mapping(target = "level", ignore = true)
    GroupDTO toDtoWithoutLevel(Group entity);

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

    // Collection mappings
    @IterableMapping(qualifiedByName = "toDto")
    List<GroupDTO> toDtoList(List<Group> entities);

}
