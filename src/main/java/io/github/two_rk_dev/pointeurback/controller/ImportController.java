package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.datasync.ImportMapping;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportResponse;
import io.github.two_rk_dev.pointeurback.exception.InvalidFileFormatException;
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
    public ResponseEntity<String> importFile(@RequestPart("metadata") ImportMapping mapping,
                                             @RequestPart("files") MultipartFile[] file) {
        importService.batchImport(file, mapping);
        return ResponseEntity.ok("Import finished.");
    }

    @PostMapping("/batch-upload")
    public ResponseEntity<ImportResponse> importMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            ImportResponse response = importService.importMultipleFiles(files);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new InvalidFileFormatException("Batch import failed due to I/O error", e);
        }
    }
}
