package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.GroupDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateGroupDTO;
import io.github.two_rk_dev.pointeurback.service.implementation.GroupServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/levels/{levelId}/groups")
public class  GroupController {

    @Autowired
    private GroupServiceImpl groupService;

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long levelId,
            @PathVariable Long groupId) {

        groupService.deleteGroup(levelId, groupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDTO> getGroup(
            @PathVariable Long levelId,
            @PathVariable Long groupId) {

        GroupDTO group = groupService.getGroupByLevel(levelId, groupId);
        return ResponseEntity.ok(group);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDTO> updateGroup(
            @PathVariable Long levelId,
            @PathVariable Long groupId,
            @Valid @RequestBody UpdateGroupDTO dto) {

        GroupDTO updatedGroup = groupService.updateGroup(levelId, groupId, dto);
        return ResponseEntity.ok(updatedGroup);
    }
}
