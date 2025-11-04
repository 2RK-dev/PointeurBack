package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.CreateTeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.TeacherDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateTeacherDTO;
import io.github.two_rk_dev.pointeurback.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    public final TeacherService teacherService;

    @GetMapping
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        List<TeacherDTO> teachers = teacherService.getAll();
        return ResponseEntity.ok(teachers);
    }

    @PostMapping
    public ResponseEntity<TeacherDTO> createTeacher(@Valid @RequestBody CreateTeacherDTO dto) {
        TeacherDTO createdTeacher = teacherService.createTeacher(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{teacherId}")
                .buildAndExpand(createdTeacher.id())
                .toUri();
        return ResponseEntity.created(location).body(createdTeacher);
    }

    @GetMapping("/{teacherId}")
    public ResponseEntity<TeacherDTO> getTeacher(@PathVariable Long teacherId) {
        TeacherDTO teacher = teacherService.getTeacher(teacherId);
        return ResponseEntity.ok(teacher);
    }

    @PutMapping("/{teacherId}")
    public ResponseEntity<TeacherDTO> updateTeacher(
            @PathVariable Long teacherId,
            @Valid @RequestBody UpdateTeacherDTO dto) {
        TeacherDTO updatedTeacher = teacherService.updateTeacher(teacherId, dto);
        return ResponseEntity.ok(updatedTeacher);
    }

    @DeleteMapping("/{teacherId}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long teacherId) {
        teacherService.deleteTeacher(teacherId);
        return ResponseEntity.noContent().build();
    }
}

