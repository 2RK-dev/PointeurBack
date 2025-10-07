package io.github.two_rk_dev.pointeurback.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.mapper.*;
import io.github.two_rk_dev.pointeurback.repository.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ExportService {

    private final RoomRepository roomRepository;
    private final TeacherRepository teacherRepository;
    private final TeachingUnitRepository teachingUnitRepository;
    private final GroupRepository groupRepository;
    private final LevelRepository levelRepository;

    private final RoomMapper roomMapper;
    private final TeacherMapper teacherMapper;
    private final TeachingUnitMapper teachingUnitMapper;
    private final GroupMapper groupMapper;
    private final LevelMapper levelMapper;
    private final ObjectMapper objectMapper;

    public ExportService(RoomRepository roomRepository,
                         TeacherRepository teacherRepository,
                         TeachingUnitRepository teachingUnitRepository,
                         GroupRepository groupRepository,
                         LevelRepository levelRepository,
                         RoomMapper roomMapper,
                         TeacherMapper teacherMapper,
                         TeachingUnitMapper teachingUnitMapper,
                         GroupMapper groupMapper,
                         LevelMapper levelMapper,
                         ObjectMapper objectMapper) {
        this.roomRepository = roomRepository;
        this.teacherRepository = teacherRepository;
        this.teachingUnitRepository = teachingUnitRepository;
        this.groupRepository = groupRepository;
        this.levelRepository = levelRepository;
        this.roomMapper = roomMapper;
        this.teacherMapper = teacherMapper;
        this.teachingUnitMapper = teachingUnitMapper;
        this.groupMapper = groupMapper;
        this.levelMapper = levelMapper;
        this.objectMapper = objectMapper;
    }

    public byte[] exportAsJson(List<String> entities) throws IOException {
        Map<String, Object> out = new LinkedHashMap<>();
        for (String e : entities) {
            String key = e.trim();
            switch (key.toLowerCase()) {
                case "room":
                    out.put("rooms", roomMapper.toDtoList(roomRepository.findAll()));
                    break;
                case "teacher":
                    out.put("teachers", teacherMapper.toDTOList(teacherRepository.findAll()));
                    break;
                case "teachingunit":
                case "teaching_unit":
                    out.put("teachingUnits", teachingUnitMapper.toDto(teachingUnitRepository.findAll()));
                    break;
                case "group":
                    out.put("groups", groupMapper.toDtoList(groupRepository.findAll()));
                    break;
                case "level":
                    out.put("levels", levelMapper.toDtoList(levelRepository.findAll()));
                    break;
                default:
                    // ignore unknown
            }
        }
        return objectMapper.writeValueAsBytes(out);
    }

    public byte[] exportAsExcel(List<String> entities) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (String e : entities) {
                String key = e.trim();
                List<Map<String, Object>> rows = fetchAsMaps(key);
                if (rows == null) continue;
                String sheetName = sanitizeSheetName(key);
                Sheet sheet = workbook.createSheet(sheetName);
                if (rows.isEmpty()) continue;
                // header
                Row header = sheet.createRow(0);
                List<String> headers = new ArrayList<>(rows.get(0).keySet());
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(headers.get(i));
                }
                // rows
                for (int r = 0; r < rows.size(); r++) {
                    Row row = sheet.createRow(r + 1);
                    Map<String, Object> map = rows.get(r);
                    for (int c = 0; c < headers.size(); c++) {
                        Cell cell = row.createCell(c);
                        Object val = map.get(headers.get(c));
                        cell.setCellValue(val == null ? "" : val.toString());
                    }
                }
            }
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] exportAsCsvOrZip(List<String> entities) throws IOException {
        // If only one entity requested, produce a single CSV (bytes). If multiple, produce a zip with separate CSV files.
        if (entities.size() == 1) {
            String key = entities.get(0).trim();
            List<Map<String, Object>> rows = fetchAsMaps(key);
            return buildCsvBytes(rows);
        } else {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (String e : entities) {
                    String key = e.trim();
                    List<Map<String, Object>> rows = fetchAsMaps(key);
                    byte[] csv = buildCsvBytes(rows);
                    String entryName = key + ".csv";
                    zos.putNextEntry(new ZipEntry(entryName));
                    zos.write(csv);
                    zos.closeEntry();
                }
                zos.finish();
                return baos.toByteArray();
            }
        }
    }

    // helper: fetch entity as list of maps (key -> value) using mappers
    private List<Map<String, Object>> fetchAsMaps(String key) {
        switch (key.toLowerCase()) {
            case "room":
                return toMaps(roomMapper.toDtoList(roomRepository.findAll()));
            case "teacher":
                return toMaps(teacherMapper.toDTOList(teacherRepository.findAll()));
            case "teachingunit":
            case "teaching_unit":
                return toMaps(teachingUnitMapper.toDto(teachingUnitRepository.findAll()));
            case "group":
                return toMaps(groupMapper.toDtoList(groupRepository.findAll()));
            case "level":
                return toMaps(levelMapper.toDtoList(levelRepository.findAll()));
            default:
                return null;
        }
    }

    private List<Map<String, Object>> toMaps(Object dtoList) {
        // convert DTO list to List<Map<String,Object>> via ObjectMapper
        return objectMapper.convertValue(dtoList, new TypeReference<List<Map<String, Object>>>() {});
    }

    private byte[] buildCsvBytes(List<Map<String, Object>> rows) throws IOException {
        if (rows == null) rows = Collections.emptyList();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (rows.isEmpty()) return new byte[0];
        // collect headers
        LinkedHashSet<String> headers = new LinkedHashSet<>();
        for (Map<String, Object> row : rows) headers.addAll(row.keySet());
        // write header
        String headerLine = String.join(",", headers) + "\n";
        baos.write(headerLine.getBytes());
        // write rows
        for (Map<String, Object> row : rows) {
            List<String> vals = new ArrayList<>();
            for (String h : headers) {
                Object v = row.get(h);
                String s = v == null ? "" : v.toString();
                // escape quotes
                if (s.contains(",") || s.contains("\n") || s.contains("\"")) {
                    s = s.replace("\"", "\"\"");
                    s = "\"" + s + "\"";
                }
                vals.add(s);
            }
            String line = String.join(",", vals) + "\n";
            baos.write(line.getBytes());
        }
        return baos.toByteArray();
    }

    private String sanitizeSheetName(String name) {
        if (name == null) return "Sheet";
        // Excel limits sheet names to 31 characters and disallows some chars
    String s = name.replaceAll("[\\\\/?*:\\[\\]]", "_");
        s = s.length() > 31 ? s.substring(0, 31) : s;
        return s;
    }
}
