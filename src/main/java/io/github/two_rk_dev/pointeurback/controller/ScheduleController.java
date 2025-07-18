package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.ScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.dto.UpdateScheduleItemDTO;
import io.github.two_rk_dev.pointeurback.service.implementation.ScheduleServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleServiceImpl scheduleService;
    @GetMapping
    public ResponseEntity<List<ScheduleItemDTO>> getSchedule(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<ScheduleItemDTO> schedule = scheduleService.getSchedule(startDate, endDate);
        return ResponseEntity.ok(schedule);
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

