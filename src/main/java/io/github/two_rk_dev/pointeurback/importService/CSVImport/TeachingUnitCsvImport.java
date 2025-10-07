package io.github.two_rk_dev.pointeurback.importService.CSVImport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import io.github.two_rk_dev.pointeurback.importService.FileImport;
import io.github.two_rk_dev.pointeurback.dto.CreateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.service.implementation.TeachingUnitServiceImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TeachingUnitCsvImport implements FileImport {
    private final TeachingUnitServiceImpl teachingUnitServiceImpl;
    private final ObjectMapper mapper;

    public TeachingUnitCsvImport(TeachingUnitServiceImpl teachingUnitServiceImpl, ObjectMapper mapper) {
        this.teachingUnitServiceImpl = teachingUnitServiceImpl;
        this.mapper = mapper;
    }

    @Override
    public boolean supports(String entityType, String fileExtension) {
        return "teachingunit".equalsIgnoreCase(entityType) && "csv".equalsIgnoreCase(fileExtension);
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
            CreateTeachingUnitDTO[] dtos = mapper.convertValue(rows, CreateTeachingUnitDTO[].class);
            teachingUnitServiceImpl.saveTeachingUnits(dtos);
        } catch (Exception e) {
            throw new RuntimeException("Failed to import teaching unit CSV: " + e.getMessage(), e);
        }
    }
}
