package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.*;
import io.github.two_rk_dev.pointeurback.service.implementation.LevelServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/levels")
public class LevelController {

    @Autowired
    private LevelServiceImpl levelService;



    @PostMapping
    public ResponseEntity<LevelDTO> createLevel(@Valid @RequestBody CreateLevelDTO dto) {
        LevelDTO createdLevel = levelService.createLevel(dto);
        return ResponseEntity.ok(createdLevel);
    }

    @GetMapping
    public ResponseEntity<List<LevelDTO>> getAllLevels() {
        List<LevelDTO> levels = levelService.getAll();
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
        List<GroupDTO> groups = levelService.getGroup(levelId);
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/{levelId}/groups")
    public ResponseEntity<GroupDTO> createGroup(@PathVariable Long levelId, @Valid @RequestBody CreateGroupDTO dto) {
        GroupDTO createdGroup = levelService.createGroup(levelId, dto);
        return ResponseEntity.ok(createdGroup);
    }

    @GetMapping("/{levelId}/teachingUnits")
    public ResponseEntity<List<TeachingUnitDTO>> getTeachingUnits(@PathVariable Long levelId) {
        List<TeachingUnitDTO> teachingUnits = levelService.getTeachingUnit(levelId);
        return ResponseEntity.ok(teachingUnits);
    }

    @PostMapping("/{levelId}/schedule")
    public ResponseEntity<ScheduleItemDTO> addScheduleItem(@PathVariable Long levelId, @Valid @RequestBody CreateScheduleItemDTO dto) {
        ScheduleItemDTO createdSchedule = levelService.addScheduleItem(levelId,dto);
        return ResponseEntity.ok(createdSchedule);
    }
}