package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.BatchCreateResponse;
import io.github.two_rk_dev.pointeurback.dto.CreateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.ScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/schedule", "/integration/schedule"})
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleItemDTO>> getSchedule(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Long groupId) {
        List<ScheduleItemDTO> schedule = scheduleService.getSchedule(levelId, groupId, startDate, endDate);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleItemDTO> getSchedule(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.getScheduleById(scheduleId));
    }

    @PostMapping
    public ResponseEntity<ScheduleItemDTO> addScheduleItem(@Valid @RequestBody CreateScheduleItemDTO dto) {
        ScheduleItemDTO createdSchedule = scheduleService.addScheduleItem(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/schedule/{schedule_item_id}")
                .buildAndExpand(createdSchedule.id())
                .toUri();
        return ResponseEntity.created(location).body(createdSchedule);
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchCreateResponse<ScheduleItemDTO, CreateScheduleItemDTO>> addScheduleItems(
            @Valid @RequestBody List<CreateScheduleItemDTO> dtos) {

        return ResponseEntity.ok(scheduleService.addScheduleItems(dtos));
    }

    @PutMapping("/{schedule_item_id}")
    public ResponseEntity<ScheduleItemDTO> updateSchedule(
            @PathVariable("schedule_item_id") Long scheduleItemId,
            @Valid @RequestBody UpdateScheduleItemDTO dto) {
        ScheduleItemDTO updatedItem = scheduleService.updateScheduleItem(scheduleItemId, dto);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{schedule_item_id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable("schedule_item_id") Long scheduleItemId) {
        scheduleService.deleteScheduleItem(scheduleItemId);
        return ResponseEntity.noContent().build();
    }
}

