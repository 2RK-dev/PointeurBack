package io.github.two_rk_dev.pointeurback.importService.ExcelImport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import io.github.two_rk_dev.pointeurback.importService.FileImport;
import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.service.implementation.RoomServiceImpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class RoomExcelImport implements FileImport {
    private final RoomServiceImpl roomServiceImpl;
    private final ObjectMapper mapper;

    public RoomExcelImport(RoomServiceImpl roomServiceImpl, ObjectMapper mapper) {
        this.roomServiceImpl = roomServiceImpl;
        this.mapper = mapper;
    }

    @Override
    public boolean supports(String entityType, String fileExtension) {
        return "room".equalsIgnoreCase(entityType) && ("xlsx".equalsIgnoreCase(fileExtension) || "xls".equalsIgnoreCase(fileExtension));
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
            CreateRoomDTO[] dtos = mapper.convertValue(mappedRows, CreateRoomDTO[].class);
            roomServiceImpl.saveRooms(dtos);
        } catch (Exception e) {
            throw new RuntimeException("Failed to import room Excel: " + e.getMessage(), e);
        }
    }
}
