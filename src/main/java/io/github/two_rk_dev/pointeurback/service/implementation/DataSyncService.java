package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.datasync.filecodec.FileCodec;
import io.github.two_rk_dev.pointeurback.datasync.mapper.EntityTableAdapter;
import io.github.two_rk_dev.pointeurback.dto.datasync.*;
import io.github.two_rk_dev.pointeurback.exception.UnknownEntityException;
import io.github.two_rk_dev.pointeurback.service.ExportService;
import io.github.two_rk_dev.pointeurback.service.ImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncService implements ImportService, ExportService {
    private final Map<String, FileCodec> codecs;
    private final Map<String, EntityTableAdapter> entityAdapters;

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
    public ImportSummary batchImport(MultipartFile @NotNull [] files, ImportMapping mapping, boolean ignoreConflicts) {
        ImportContext context = new ImportContext(ignoreConflicts);
        for (MultipartFile file : files) {
            processFile(file, mapping, context);
        }
        finalizeImport(context);
        return context.summary;
    }

    private void processFile(@NotNull MultipartFile file, @NotNull ImportMapping mapping, ImportContext context) {
        String filename = file.getOriginalFilename();
        Map<String, TableMapping> fileMapping = mapping.metadata().get(filename);

        if (fileMapping == null) {
            context.summary.skippedFiles().add(filename);
            return;
        }

        try {
            FileCodec fileCodec = getFileCodec(file);
            List<@NotNull TableData> decoded = fileCodec.decode(file.getInputStream());
            for (TableData tableData : decoded) {
                processTable(tableData, fileMapping, filename, context);
            }
        } catch (IOException | UnknownEntityException e) {
            context.summary.errors().add(SyncError.forEntity("SYSTEM", -1,
                    "File processing failed: " + e.getMessage(), filename));
            context.summary.skippedFiles().add(filename);
        }
    }

    private FileCodec getFileCodec(@NotNull MultipartFile file) {
        String codecType = FileCodec.Type.forInputMediaType(file.getContentType()).beanName();
        return codecs.get(codecType);
    }

    private void processTable(@NotNull TableData tableData, @NotNull Map<String, TableMapping> fileMapping,
                              String filename, ImportContext context) {
        TableMapping tableMapping = fileMapping.get(tableData.tableName());
        String subfileFullName = "%s/%s".formatted(filename, tableData.tableName());
        if (tableMapping == null) {
            context.summary.skippedFiles().add(subfileFullName);
            return;
        }
        List<String> newHeaders = tableData.headers().stream()
                .map(h -> tableMapping.headersMapping().get(h))
                .toList();

        EntityTableAdapter adapter = getEntityAdapter(tableMapping);
        context.usedAdapters.add(adapter);

        List<SyncError> syncErrors = adapter.process(
                context.stageID,
                tableData.withTableInfo(subfileFullName, newHeaders),
                context.ignoreConflicts
        );

        EntityTableAdapter.@NotNull Type entityType = adapter.getEntityType();
        context.summary.errors().addAll(syncErrors);
        context.summary.updateTotalRows(old -> old + tableData.rows().size());

        int successful = tableData.rows().size() - syncErrors.size();
        context.summary.updateSuccessfulRows(old -> old + successful);
        context.summary.entitySummary().merge(entityType.entityName(), successful, Integer::sum);
    }

    private EntityTableAdapter getEntityAdapter(@NotNull TableMapping tableMapping) {
        EntityTableAdapter.Type entityType = EntityTableAdapter.Type.forEntity(tableMapping.entityType());
        return entityAdapters.get(entityType.beanName());
    }

    private void finalizeImport(@NotNull ImportContext context) {
        for (EntityTableAdapter adapter : context.usedAdapters) {
            List<SyncError> syncErrors = adapter.finalize(context.stageID, context.ignoreConflicts);
            context.summary.errors().addAll(syncErrors);
            context.summary.updateSuccessfulRows(old -> old - syncErrors.size());
            context.summary.entitySummary().merge(
                    adapter.getEntityType().entityName(),
                    syncErrors.size(),
                    (val, newVal) -> val - newVal
            );
        }
    }

    private static class ImportContext {
        private final Set<EntityTableAdapter> usedAdapters = new HashSet<>();
        private final ImportSummary summary = new ImportSummary();
        private final boolean ignoreConflicts;
        private final UUID stageID = UUID.randomUUID();

        public ImportContext(boolean ignoreConflicts) {
            this.ignoreConflicts = ignoreConflicts;
        }
    }
}
