package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.importService.FileImportContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/import")
public class ImportController {

    private final FileImportContext context;

    public ImportController(FileImportContext context) {
        this.context = context;
    }

    @PostMapping("/{entityType}/upload")
    public ResponseEntity<String> importFile(@PathVariable("entityType") String entityType,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam(value = "levelId", required = false) Long levelId) {
        Objects.requireNonNull(file, "file is required");
        context.importFile(entityType, file);
        return ResponseEntity.ok("File imported for entity: " + entityType);
    }
}
