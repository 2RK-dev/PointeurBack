package io.github.two_rk_dev.pointeurback.importService.CSVImport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import io.github.two_rk_dev.pointeurback.importService.FileImport;
import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.service.implementation.RoomServiceImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RoomCsvImport implements FileImport {
    private final RoomServiceImpl roomServiceImpl;
    private final ObjectMapper mapper;

    public RoomCsvImport(RoomServiceImpl roomServiceImpl, ObjectMapper mapper) {
        this.roomServiceImpl = roomServiceImpl;
        this.mapper = mapper;
    }

    @Override
    public boolean supports(String entityType, String fileExtension) {
        return "room".equalsIgnoreCase(entityType) && "csv".equalsIgnoreCase(fileExtension);
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
            CreateRoomDTO[] dtos = mapper.convertValue(rows, CreateRoomDTO[].class);
            roomServiceImpl.saveRooms(dtos);
        } catch (Exception e) {
            throw new RuntimeException("Failed to import room CSV: " + e.getMessage(), e);
        }
    }
}
