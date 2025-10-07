package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping
    public ResponseEntity<byte[]> export(@RequestParam("entities") String entitiesCsv,
                                         @RequestParam("format") String format) throws Exception {
        var list = Arrays.stream(entitiesCsv.split(",")).map(String::trim).toList();
        String joined = String.join("_", list).replaceAll("\\s+", "_");
        String date = java.time.LocalDate.now().toString();
        String filename;
        byte[] data;
        MediaType contentType;

        switch (format.toLowerCase()) {
            case "json":
                data = exportService.exportAsJson(list);
                filename = String.format("export_%s_%s.json", date, joined);
                contentType = MediaType.APPLICATION_JSON;
                break;
            case "excel":
            case "xlsx":
                data = exportService.exportAsExcel(list);
                filename = String.format("export_%s_%s.xlsx", date, joined);
                contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                break;
            case "csv":
                data = exportService.exportAsCsvOrZip(list);
                filename = list.size() == 1 ? String.format("export_%s_%s.csv", date, joined) : String.format("export_%s_%s.zip", date, joined);
                contentType = list.size() == 1 ? MediaType.parseMediaType("text/csv") : MediaType.APPLICATION_OCTET_STREAM;
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(contentType)
                .body(data);
    }

}
