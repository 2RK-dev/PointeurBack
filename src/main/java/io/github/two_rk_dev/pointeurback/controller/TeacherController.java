package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.CreateTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.TeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeacherDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
public class TeacherController {
    @GetMapping
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PostMapping
    public ResponseEntity<TeacherDTO> createTeacher(@Valid @RequestBody CreateTeacherDTO dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @GetMapping("/{teacherId}")
    public ResponseEntity<TeacherDTO> getTeacher(@PathVariable Long teacherId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PutMapping("/{teacherId}")
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable Long teacherId, @Valid @RequestBody UpdateTeacherDTO dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @DeleteMapping("/{teacherId}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long teacherId) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

