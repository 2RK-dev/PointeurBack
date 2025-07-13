package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.CreateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.TeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.service.implementation.TeachingUnitServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachingUnits")
public class TeachingUnitController {

    @Autowired
    private TeachingUnitServiceImpl teachingUnitService;

    @GetMapping
    public ResponseEntity<List<TeachingUnitDTO>> getAllTeachingUnits() {
        List<TeachingUnitDTO> units = teachingUnitService.getAll();
        return ResponseEntity.ok(units);
    }

    @PostMapping
    public ResponseEntity<TeachingUnitDTO> createTeachingUnit(@Valid @RequestBody CreateTeachingUnitDTO dto) {
        TeachingUnitDTO createdUnit = teachingUnitService.createTeachingUnit(dto);
        return ResponseEntity.ok(createdUnit);
    }

    @GetMapping("/{unitId}")
    public ResponseEntity<TeachingUnitDTO> getTeachingUnit(@PathVariable Long unitId) {
        TeachingUnitDTO unit = teachingUnitService.getTeachingUnit(unitId);
        return ResponseEntity.ok(unit);
    }

    @PutMapping("/{unitId}")
    public ResponseEntity<TeachingUnitDTO> updateTeachingUnit(
            @PathVariable Long unitId,
            @Valid @RequestBody UpdateTeachingUnitDTO dto) {
        TeachingUnitDTO updatedUnit = teachingUnitService.updateTeachingUnit(unitId, dto);
        return ResponseEntity.ok(updatedUnit);
    }

    @DeleteMapping("/{unitId}")
    public ResponseEntity<Void> deleteTeachingUnit(@PathVariable Long unitId) {
        teachingUnitService.deleteTeachingUnit(unitId);
        return ResponseEntity.noContent().build();
    }
}

