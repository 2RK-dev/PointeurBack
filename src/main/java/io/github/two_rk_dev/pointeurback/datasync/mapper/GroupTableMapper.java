package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.CreateGroupDTO;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.GroupService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("group_table_mapper")
public class GroupTableMapper implements EntityTableMapper {
    private static final Map<String, CsvFieldBinding<CreateGroupDTO>> DEFAULT_MAPPING = Utils.buildMapping(CreateGroupDTO.class);
    private final GroupService groupService;
    private final ObjectMapper objectMapper;

    public GroupTableMapper(GroupService groupService, ObjectMapper objectMapper) {
        this.groupService = groupService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void persist(@NotNull TableData tableData) {
        CreateGroupDTO[] dtos = Utils.parseDTOs(tableData, CreateGroupDTO.class, DEFAULT_MAPPING, objectMapper);
        Map<Long, List<CreateGroupDTO>> groupedByLevelId = new HashMap<>();
        for (CreateGroupDTO dto : dtos) {
            groupedByLevelId.computeIfAbsent(dto.levelId(), k -> new ArrayList<>()).add(dto);
        }
        groupService.saveGroups(groupedByLevelId);
    }

    @Override
    public @NotNull TableData fetch() {
        List<String> headers = Utils.getHeaders(DEFAULT_MAPPING);
        List<@NotNull List<String>> rows = Utils.toRows(groupService.getAll());
        return new TableData(Type.GROUP.entityName(), headers, rows);
    }
}
