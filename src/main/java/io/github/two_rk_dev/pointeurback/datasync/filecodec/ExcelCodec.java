package io.github.two_rk_dev.pointeurback.datasync.filecodec;

import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * A component for encoding and decoding a dataset to and from Excel files.
 */
@Component("excel_codec")
public class ExcelCodec implements FileCodec {

    @Override
    public byte[] encode(@NotNull List<TableData> dataset) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (TableData tableData : dataset) {
                String sheetName = sanitizeSheetName(tableData.tableName());
                Sheet sheet = workbook.createSheet(sheetName);
                if (tableData.rows().isEmpty()) continue;

                Row header = sheet.createRow(0);
                List<String> headers = tableData.headers();
                for (int i = 0; i < headers.size(); i++) {
                    header.createCell(i).setCellValue(headers.get(i));
                }

                for (int r = 0; r < tableData.rows().size(); r++) {
                    Row row = sheet.createRow(r + 1);
                    for (int c = 0; c < headers.size(); c++) {
                        String val = tableData.rows().get(r).get(c);
                        row.createCell(c).setCellValue(val == null ? "" : val);
                    }
                }
            }
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    /**
     * Decodes the first sheet.
     */
    @Override
    public List<@NotNull TableData> decode(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            List<TableData> tableDataList = new ArrayList<>();
            for (Sheet sheet : workbook) tableDataList.add(parseSheet(sheet, evaluator));
            return tableDataList;
        }
    }

    @Override
    public @NotNull Type getType() {
        return Type.EXCEL;
    }

    @Contract("_, _ -> new")
    private @NotNull TableData parseSheet(@NotNull Sheet sheet, FormulaEvaluator evaluator) {
        Iterator<Row> rows = sheet.iterator();
        if (!rows.hasNext()) return TableData.EMPTY;

        DataFormatter formatter = new DataFormatter();
        List<String> headers = new ArrayList<>();
        List<Integer> headerColumnIndices = new ArrayList<>();
        rows.next().forEach(c -> {
            String cellValue = formatter.formatCellValue(c, evaluator);
            if (cellValue == null || cellValue.isBlank()) return;
            headerColumnIndices.add(c.getColumnIndex());
            headers.add(cellValue);
        });

        List<List<String>> data = new ArrayList<>();
        rows.forEachRemaining(row -> {
            List<String> rowValues = new ArrayList<>();
            for (int i : headerColumnIndices) {
                rowValues.add(Optional.ofNullable(row.getCell(i))
                        .map(c -> formatter.formatCellValue(c, evaluator))
                        .orElse(null)
                );
            }
            data.add(rowValues);
        });
        return new TableData(sheet.getSheetName(), headers, data);
    }

    /**
     * Sanitizes a sheet name to comply with Excel's sheet naming restrictions.
     *
     * @param name the original sheet name
     * @return a sanitized sheet name
     */
    private String sanitizeSheetName(String name) {
        if (name == null) return "Sheet";
        String s = name.replaceAll("[\\\\/?*:\\[\\]]", "_");
        s = s.length() > 31 ? s.substring(0, 31) : s;
        return s;
    }
}