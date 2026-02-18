package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.*;
import io.github.two_rk_dev.pointeurback.service.LevelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @PostMapping
    public ResponseEntity<LevelDTO> createLevel(@Valid @RequestBody CreateLevelDTO dto) {
        LevelDTO createdLevel = levelService.createLevel(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{levelId}")
                .buildAndExpand(createdLevel.id())
                .toUri();
        return ResponseEntity.created(location).body(createdLevel);
    }

    @GetMapping
    public ResponseEntity<List<LevelDetailsDTO>> getAllLevels() {
        List<LevelDetailsDTO> levels = levelService.getAllDetailed();
        return ResponseEntity.ok(levels);
    }

    @GetMapping("/{levelId}")
    public ResponseEntity<LevelDetailsDTO> getLevel(@PathVariable Long levelId) {
        LevelDetailsDTO levelDetails = levelService.getDetails(levelId);
        return ResponseEntity.ok(levelDetails);
    }

    @PutMapping("/{levelId}")
    public ResponseEntity<LevelDTO> updateLevel(@PathVariable Long levelId, @Valid @RequestBody UpdateLevelDTO dto) {
        LevelDTO updatedLevel = levelService.updateLevel(levelId, dto);
        return ResponseEntity.ok(updatedLevel);
    }

    @DeleteMapping("/{levelId}")
    public ResponseEntity<Void> deleteLevel(@PathVariable Long levelId) {
        levelService.deleteLevel(levelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{levelId}/groups")
    public ResponseEntity<List<GroupDTO>> getGroups(@PathVariable Long levelId) {
        List<GroupDTO> groups = levelService.getGroups(levelId);
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/{levelId}/groups")
    public ResponseEntity<GroupDTO> createGroup(@PathVariable Long levelId, @Valid @RequestBody CreateGroupDTO dto) {
        GroupDTO createdGroup = levelService.createGroup(levelId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{groupId}")
                .buildAndExpand(createdGroup.id())
                .toUri();
        return ResponseEntity.created(location).body(createdGroup);
    }

    @GetMapping("/{levelId}/teachingUnits")
    public ResponseEntity<List<TeachingUnitDTO>> getTeachingUnits(@PathVariable Long levelId) {
        List<TeachingUnitDTO> teachingUnits = levelService.getTeachingUnits(levelId);
        return ResponseEntity.ok(teachingUnits);
    }
}