package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.datasync.ImportMapping;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportSummary;
import io.github.two_rk_dev.pointeurback.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
public class ImportController {
    private final ImportService importService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportSummary> importFile(@RequestPart("metadata") ImportMapping mapping,
                                                    @RequestPart("files") MultipartFile[] file,
                                                    @RequestParam(defaultValue = "true") boolean ignoreConflicts) {
        ImportSummary response = importService.batchImport(file, mapping, ignoreConflicts);
        return ResponseEntity.ok(response);
    }
}
