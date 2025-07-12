package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.GroupDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateGroupDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/levels/{levelId}/groups")
public class GroupController {
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long levelId, @PathVariable Long groupId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDTO> getGroup(@PathVariable Long levelId, @PathVariable Long groupId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable Long levelId, @PathVariable Long groupId, @RequestBody UpdateGroupDTO dto) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

