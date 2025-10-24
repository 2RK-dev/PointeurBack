package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.datasync.ImportResponse;
import io.github.two_rk_dev.pointeurback.exception.InvalidFileFormatException;
import io.github.two_rk_dev.pointeurback.service.ImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/import")
public class ImportController {
    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/{entityType}/upload")
    public ResponseEntity<String> importFile(@PathVariable("entityType") String entityType,
                                             @RequestParam("file") MultipartFile file) {
        try {
            importService.import_(entityType, file);
        } catch (IOException e) {
            throw new InvalidFileFormatException("Corrupted file: %s or wrong content type".formatted(file.getOriginalFilename()), e);
        }
        return ResponseEntity.ok("File imported for entity: " + entityType);
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
