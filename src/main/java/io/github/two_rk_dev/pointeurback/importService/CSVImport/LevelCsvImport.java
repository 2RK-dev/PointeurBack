package io.github.two_rk_dev.pointeurback.importService.CSVImport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import io.github.two_rk_dev.pointeurback.importService.FileImport;
import io.github.two_rk_dev.pointeurback.dto.CreateLevelDTO;
import io.github.two_rk_dev.pointeurback.service.implementation.LevelServiceImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LevelCsvImport implements FileImport {
    private final LevelServiceImpl levelServiceImpl;
    private final ObjectMapper mapper;

    public LevelCsvImport(LevelServiceImpl levelServiceImpl, ObjectMapper mapper) {
        this.levelServiceImpl = levelServiceImpl;
        this.mapper = mapper;
    }

    @Override
    public boolean supports(String entityType, String fileExtension) {
        return "level".equalsIgnoreCase(entityType) && "csv".equalsIgnoreCase(fileExtension);
    }

    @Override
    public void importData(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return;
            String[] headers = headerLine.split(",");
            List<Map<String, String>> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",");
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < headers.length && i < cols.length; i++) {
                    map.put(headers[i].trim(), cols[i].trim());
                }
                rows.add(map);
            }
            CreateLevelDTO[] dtos = mapper.convertValue(rows, CreateLevelDTO[].class);
            if (dtos != null) {
                for (CreateLevelDTO dto : dtos) {
                    levelServiceImpl.createLevel(dto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to import level CSV: " + e.getMessage(), e);
        }
    }
}
