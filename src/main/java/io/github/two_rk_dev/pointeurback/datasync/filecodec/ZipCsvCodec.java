package io.github.two_rk_dev.pointeurback.datasync.filecodec;

import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A component for encoding a dataset into zipped CSV files and decoding a table's data from a CSV.
 */
@Component("zip_csv_codec")
public class ZipCsvCodec implements FileCodec {

    @Override
    public byte[] encode(@NotNull List<TableData> dataSet) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (TableData tableData : dataSet) {
                byte[] csv = buildCsvBytes(tableData);
                String entryName = tableData.tableName() + ".csv";
                zos.putNextEntry(new ZipEntry(entryName));
                zos.write(csv);
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        }
    }

    @Override
    public List<@NotNull TableData> decode(InputStream inputStream) throws IOException {
        return List.of(parseCsv(inputStream, ""));
    }

    @Override
    public @NotNull Type getType() {
        return Type.ZIP_CSV;
    }

    @Contract("_, _ -> new")
    private @NotNull TableData parseCsv(InputStream inputStream, @SuppressWarnings("SameParameterValue") String csvFileName) throws IOException {
        CSVFormat csvFormat = CSVFormat.Builder.create(CSVFormat.DEFAULT).setHeader().setSkipHeaderRecord(true).get();
        CSVParser parser = CSVParser.parse(new InputStreamReader(inputStream), csvFormat);
        List<String> headers = parser.getHeaderNames();
        List<List<String>> rows = parser.stream().map(CSVRecord::toList).toList();
        return new TableData(csvFileName, headers, rows);
    }

    private byte @NotNull [] buildCsvBytes(@NotNull TableData tableData) throws IOException {
        if (tableData.rows() == null || tableData.rows().isEmpty()) return new byte[0];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(baos), CSVFormat.DEFAULT)) {
            csvPrinter.printRecord(tableData.headers());
            csvPrinter.printRecords(tableData.rows());
        }
        return baos.toByteArray();
    }
}