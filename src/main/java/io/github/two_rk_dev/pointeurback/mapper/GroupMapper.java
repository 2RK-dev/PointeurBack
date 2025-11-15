package io.github.two_rk_dev.pointeurback.mapper;

import io.github.two_rk_dev.pointeurback.dto.CreateGroupDTO;
import io.github.two_rk_dev.pointeurback.dto.GroupDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateGroupDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportGroupDTO;
import io.github.two_rk_dev.pointeurback.model.Group;
import io.github.two_rk_dev.pointeurback.model.Level;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LevelMapper.class})
public interface GroupMapper {

    @Named("toDto")
    @Mapping(target = "level", qualifiedBy = MappingQualifier.LevelToDtoWithoutGroups.class)
    GroupDTO toDto(Group entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    Group fromCreateDto(CreateGroupDTO dto);

    default Group fromCreateDto(CreateGroupDTO dto, Level level) {
        Group group = fromCreateDto(dto);
        group.setLevel(level);
        return group;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    void updateFromUpdateDto(UpdateGroupDTO dto, @MappingTarget Group entity);

    @IterableMapping(qualifiedByName = "toDto")
    List<GroupDTO> toDtoList(List<Group> entities);

    @Mapping(target = "levelId", source = "level.id")
    ImportGroupDTO toExportDTO(Group entity);
}
