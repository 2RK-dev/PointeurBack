package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.GroupDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateGroupDTO;

public interface GroupService {
    GroupDTO getGroupByLevel(Long levelId, Long groupId);
    GroupDTO updateGroup(Long levelId, Long groupId, UpdateGroupDTO dto);
    Void deleteGroup(Long levelId, Long groupId);
}
