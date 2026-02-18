package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping
    public ResponseEntity<byte[]> export(@RequestParam("entitiesList") List<String> entitiesList,
                                         @RequestParam("format") String format) throws IOException {
        String joined = String.join("_", entitiesList);
        String date = java.time.LocalDate.now().toString();
        ExportService.Exported exported = exportService.export(entitiesList, format);
        String filename = "export_%s_%s.%s".formatted(joined, date, exported.fileCodec().outputFileExtension());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(exported.fileCodec().outputMediaType())
                .body(exported.data());
    }
}
