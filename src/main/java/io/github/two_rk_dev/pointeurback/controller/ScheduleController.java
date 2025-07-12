package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    @GetMapping
    public ResponseEntity<List<ScheduleItemDTO>> getSchedule(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Long groupId) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

