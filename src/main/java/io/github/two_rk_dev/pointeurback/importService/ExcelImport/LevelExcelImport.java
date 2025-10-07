package io.github.two_rk_dev.pointeurback.importService.ExcelImport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import io.github.two_rk_dev.pointeurback.importService.FileImport;
import io.github.two_rk_dev.pointeurback.dto.CreateLevelDTO;
import io.github.two_rk_dev.pointeurback.service.implementation.LevelServiceImpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class LevelExcelImport implements FileImport {
    private final LevelServiceImpl levelServiceImpl;
    private final ObjectMapper mapper;

    public LevelExcelImport(LevelServiceImpl levelServiceImpl, ObjectMapper mapper) {
        this.levelServiceImpl = levelServiceImpl;
        this.mapper = mapper;
    }

    @Override
    public boolean supports(String entityType, String fileExtension) {
        return "level".equalsIgnoreCase(entityType) && ("xlsx".equalsIgnoreCase(fileExtension) || "xls".equalsIgnoreCase(fileExtension));
    }

    @Override
    public void importData(MultipartFile file) {
        try (InputStream in = file.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (!rows.hasNext()) return;
            Row headerRow = rows.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) headers.add(cell.getStringCellValue());
            List<Map<String, String>> mappedRows = new ArrayList<>();
            while (rows.hasNext()) {
                Row row = rows.next();
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i);
                    if (cell == null) continue;
                    map.put(headers.get(i), cell.toString());
                }
                mappedRows.add(map);
            }
            CreateLevelDTO[] dtos = mapper.convertValue(mappedRows, CreateLevelDTO[].class);
            if (dtos != null) {
                for (CreateLevelDTO dto : dtos) {
                    levelServiceImpl.createLevel(dto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to import level Excel: " + e.getMessage(), e);
        }
    }
}
