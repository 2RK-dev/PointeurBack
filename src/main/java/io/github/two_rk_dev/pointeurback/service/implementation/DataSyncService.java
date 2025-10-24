package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.datasync.filecodec.FileCodec;
import io.github.two_rk_dev.pointeurback.datasync.mapper.EntityTableMapper;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportResponse;
import io.github.two_rk_dev.pointeurback.dto.datasync.SyncError;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.ExportService;
import io.github.two_rk_dev.pointeurback.service.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataSyncService implements ImportService, ExportService {
    private final Map<String, FileCodec> codecs;
    private final Map<String, EntityTableMapper> entityMappers;

    public DataSyncService(Map<String, FileCodec> codecs, Map<String, EntityTableMapper> entityMappers) {
        this.codecs = codecs;
        this.entityMappers = entityMappers;
    }

    @Override
    public void import_(@NotNull String entityName, @NotNull MultipartFile file) throws IOException {
        FileCodec fileCodec = codecs.get(FileCodec.Type.forInputMediaType(file.getContentType()).beanName());
        EntityTableMapper.Type entityType = EntityTableMapper.Type.forEntity(entityName);
        List<@NotNull TableData> decoded = fileCodec.decode(file.getInputStream());
        TableData tableData = decoded.stream()
                .filter(td -> td.tableName().equals(entityName))
                .findFirst()
                .orElseGet(decoded::getFirst);
        entityMappers.get(entityType.beanName()).persist(tableData);
    }

    @Override
    public ImportResponse importMultipleFiles(@NotNull MultipartFile[] files) throws IOException {
        Map<String, Integer> entitySummary = new HashMap<>();
        List<SyncError> allErrors = new ArrayList<>();
        int totalRows = 0;
        int successfulRows = 0;

        for (MultipartFile file : files) {
            try {
                FileImportResult result = processSingleFile(file);
                allErrors.addAll(result.errors());
                totalRows += result.totalRows();
                successfulRows += result.successfulRows();
                
                // Update entity summary
                result.entitySummary().forEach((entity, count) -> 
                    entitySummary.merge(entity, count, Integer::sum));
                
            } catch (IOException e) {
                log.error("Failed to process file: {}", file.getOriginalFilename(), e);
                allErrors.add(SyncError.forEntity("SYSTEM", -1, 
                    "File processing failed: " + e.getMessage(), file.getOriginalFilename()));
            }
        }

        return ImportResponse.withErrors(totalRows, successfulRows, allErrors, entitySummary);
    }

    private FileImportResult processSingleFile(@NotNull MultipartFile file) throws IOException {
        String fileContentType = file.getContentType();
        if (fileContentType == null) {
            throw new IOException("No content type specified for file: " + file.getOriginalFilename());
        }

        FileCodec fileCodec = codecs.get(FileCodec.Type.forInputMediaType(fileContentType).beanName());
        List<@NotNull TableData> decoded = fileCodec.decode(file.getInputStream());
        
        List<SyncError> fileErrors = new ArrayList<>();
        int fileTotalRows = 0;
        int fileSuccessfulRows = 0;
        Map<String, Integer> fileEntitySummary = new HashMap<>();

        for (TableData tableData : decoded) {
            EntityTableMapper.Type entityType = EntityTableMapper.Type.forEntity(tableData.tableName());
            EntityTableMapper mapper = entityMappers.get(entityType.beanName());
            
            try {
                mapper.persist(tableData);
                fileSuccessfulRows += tableData.rows().size();
                fileEntitySummary.merge(tableData.tableName(), tableData.rows().size(), Integer::sum);
            } catch (Exception e) {
                log.error("Failed to persist entity {} from file {}", tableData.tableName(), file.getOriginalFilename(), e);
                // Add error for each row in the table
                for (int i = 0; i < tableData.rows().size(); i++) {
                    fileErrors.add(SyncError.forEntity(
                        tableData.tableName(), 
                        i, 
                        "Persistence failed: " + e.getMessage(),
                        tableData.rows().get(i).toString()
                    ));
                }
            }
            
            fileTotalRows += tableData.rows().size();
        }

        return new FileImportResult(fileTotalRows, fileSuccessfulRows, fileErrors, fileEntitySummary);
    }

    @Override
    public Exported export(@NotNull List<String> entitiesNames, String format) throws IOException {
        FileCodec fileCodec = codecs.get(FileCodec.Type.forCodecName(format).beanName());
        List<TableData> tableDataList = new ArrayList<>();
        for (String e : entitiesNames) {
            EntityTableMapper tableMapper = entityMappers.get(EntityTableMapper.Type.forEntity(e).beanName());
            tableDataList.add(tableMapper.fetch());
        }
        return new Exported(fileCodec.encode(tableDataList), fileCodec.getType());
    }

    private record FileImportResult(
        int totalRows,
        int successfulRows,
        List<SyncError> errors,
        Map<String, Integer> entitySummary
    ) {}
}
