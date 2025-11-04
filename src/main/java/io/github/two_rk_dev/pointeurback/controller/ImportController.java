package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.datasync.ImportMapping;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportResponse;
import io.github.two_rk_dev.pointeurback.service.ImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/import")
public class ImportController {
    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ImportResponse> importFile(@RequestPart("metadata") ImportMapping mapping,
                                             @RequestPart("files") MultipartFile[] file) {
        ImportResponse response = importService.batchImport(file, mapping);
        return ResponseEntity.ok(response);
    }
}
