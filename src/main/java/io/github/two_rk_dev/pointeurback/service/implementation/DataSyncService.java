package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.datasync.filecodec.FileCodec;
import io.github.two_rk_dev.pointeurback.datasync.mapper.EntityTableAdapter;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportMapping;
import io.github.two_rk_dev.pointeurback.dto.datasync.SyncError;
import io.github.two_rk_dev.pointeurback.datasync.mapper.EntityTableMapper;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportResponse;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableMapping;
import io.github.two_rk_dev.pointeurback.service.ExportService;
import io.github.two_rk_dev.pointeurback.service.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class DataSyncService implements ImportService, ExportService {
    private final Map<String, FileCodec> codecs;
    private final Map<String, EntityTableAdapter> entityAdapters;

    public DataSyncService(Map<String, FileCodec> codecs, Map<String, EntityTableAdapter> entityAdapters) {
        this.codecs = codecs;
        this.entityAdapters = entityAdapters;
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
            EntityTableAdapter tableMapper = entityAdapters.get(EntityTableAdapter.Type.forEntity(e).beanName());
            tableDataList.add(tableMapper.fetch());
        }
        return new Exported(fileCodec.encode(tableDataList), fileCodec.getType());
    }

    @Override
    public List<SyncError> batchImport(MultipartFile @NotNull [] files, ImportMapping mapping) {
        UUID stageID = UUID.randomUUID();
        List<SyncError> errors = new ArrayList<>();
        Set<EntityTableAdapter> usedAdapters = new HashSet<>();
        for (MultipartFile file : files) {
            String codecType = FileCodec.Type.forInputMediaType(file.getContentType()).beanName();
            String filename = file.getOriginalFilename();
            FileCodec fileCodec = codecs.get(codecType);
            try {
                List<@NotNull TableData> decoded = fileCodec.decode(file.getInputStream());
                Map<String, TableMapping> fileMapping = mapping.metadata().get(filename);
                for (TableData tableData : decoded) {
                    TableMapping tableMapping = fileMapping.get(tableData.tableName());
                    EntityTableAdapter.Type entityType = EntityTableAdapter.Type.forEntity(tableMapping.entityType());
                    EntityTableAdapter adapter = entityAdapters.get(entityType.beanName());
                    usedAdapters.add(adapter);
                    String subfileFullName = "%s/%s".formatted(filename, tableData.tableName());
                    List<SyncError> syncErrors = adapter.process(stageID, tableData.withTableName(subfileFullName));
                    errors.addAll(syncErrors);
                }
            } catch (IOException e) {
                log.error("Failed to parse file {}", filename, e);
            }
        }
        for (EntityTableAdapter entityTableAdapter : usedAdapters) {
            List<SyncError> syncErrors = entityTableAdapter.finalize(stageID);
            errors.addAll(syncErrors);
        }
        return errors;
    }

    private record FileImportResult(
            int totalRows,
            int successfulRows,
            List<SyncError> errors,
            Map<String, Integer> entitySummary
    ) {
    }
}
