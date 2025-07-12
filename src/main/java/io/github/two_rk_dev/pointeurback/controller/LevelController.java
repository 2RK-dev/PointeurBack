package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/levels")
public class LevelController {
    @PostMapping
    public ResponseEntity<LevelDTO> createLevel(@Valid @RequestBody CreateLevelDTO dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @GetMapping
    public ResponseEntity<List<LevelDTO>> getAllLevels() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @GetMapping("/{levelId}")
    public ResponseEntity<LevelDetailsDTO> getLevel(@PathVariable Long levelId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PutMapping("/{levelId}")
    public ResponseEntity<LevelDTO> updateLevel(@PathVariable Long levelId, @Valid @RequestBody UpdateLevelDTO dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @DeleteMapping("/{levelId}")
    public ResponseEntity<Void> deleteLevel(@PathVariable Long levelId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @GetMapping("/{levelId}/groups")
    public ResponseEntity<List<GroupDTO>> getGroups(@PathVariable Long levelId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PostMapping("/{levelId}/groups")
    public ResponseEntity<GroupDTO> createGroup(@PathVariable Long levelId, @Valid @RequestBody CreateGroupDTO dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @GetMapping("/{levelId}/teachingUnits")
    public ResponseEntity<List<TeachingUnitDTO>> getTeachingUnits(@PathVariable Long levelId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PostMapping("/{levelId}/schedule")
    public ResponseEntity<ScheduleItemDTO> addScheduleItem(@PathVariable Long levelId, @Valid @RequestBody CreateScheduleItemDTO dto) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

