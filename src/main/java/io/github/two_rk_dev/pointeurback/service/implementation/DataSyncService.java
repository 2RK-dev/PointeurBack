package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.datasync.filecodec.FileCodec;
import io.github.two_rk_dev.pointeurback.datasync.mapper.EntityTableAdapter;
import io.github.two_rk_dev.pointeurback.dto.datasync.*;
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
    public ImportResponse batchImport(MultipartFile @NotNull [] files, ImportMapping mapping) {
        UUID stageID = UUID.randomUUID();
        Map<String, Integer> entitySummary = new HashMap<>();
        List<SyncError> errors = new ArrayList<>();
        int totalRows = 0;
        int successfulRows = 0;
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
                    totalRows += tableData.rows().size();
                    int successful = tableData.rows().size() - syncErrors.size();
                    successfulRows += successful;
                    entitySummary.merge(entityType.entityName(), successful, Integer::sum);
                }
            } catch (IOException e) {
                log.error("Failed to process file: {}", file.getOriginalFilename(), e);
                errors.add(SyncError.forEntity("SYSTEM", -1,
                        "File processing failed: " + e.getMessage(), filename));
            }
        }
        for (EntityTableAdapter adapter : usedAdapters) {
            List<SyncError> syncErrors = adapter.finalize(stageID);
            errors.addAll(syncErrors);
            successfulRows -= syncErrors.size();
            entitySummary.merge(adapter.getEntityType().entityName(), syncErrors.size(), (val, newVal) -> val - newVal);
        }
        return ImportResponse.withErrors(totalRows, successfulRows, errors, entitySummary);
    }
}
