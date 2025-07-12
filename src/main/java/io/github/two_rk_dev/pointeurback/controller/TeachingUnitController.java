package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.CreateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.TeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeachingUnitDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachingUnits")
public class TeachingUnitController {
    @GetMapping
    public ResponseEntity<List<TeachingUnitDTO>> getAllTeachingUnits() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PostMapping
    public ResponseEntity<TeachingUnitDTO> createTeachingUnit(@Valid @RequestBody CreateTeachingUnitDTO dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @GetMapping("/{unitId}")
    public ResponseEntity<TeachingUnitDTO> getTeachingUnit(@PathVariable Long unitId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PutMapping("/{unitId}")
    public ResponseEntity<TeachingUnitDTO> updateTeachingUnit(@PathVariable Long unitId, @Valid @RequestBody UpdateTeachingUnitDTO dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @DeleteMapping("/{unitId}")
    public ResponseEntity<Void> deleteTeachingUnit(@PathVariable Long unitId) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

