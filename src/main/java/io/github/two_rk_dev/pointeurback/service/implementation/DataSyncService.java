package io.github.two_rk_dev.pointeurback.service.implementation;

import io.github.two_rk_dev.pointeurback.datasync.filecodec.FileCodec;
import io.github.two_rk_dev.pointeurback.datasync.mapper.EntityTableAdapter;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.service.ExportService;
import io.github.two_rk_dev.pointeurback.service.ImportService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataSyncService implements ImportService, ExportService {
    private final Map<String, FileCodec> codecs;
    private final Map<String, EntityTableAdapter> entityMappers;

    public DataSyncService(Map<String, FileCodec> codecs, Map<String, EntityTableAdapter> entityMappers) {
        this.codecs = codecs;
        this.entityMappers = entityMappers;
    }

    @Override
    public void import_(@NotNull String entityName, @NotNull MultipartFile file) throws IOException {
        FileCodec fileCodec = codecs.get(FileCodec.Type.forInputMediaType(file.getContentType()).beanName());
        EntityTableAdapter.Type entityType = EntityTableAdapter.Type.forEntity(entityName);
        List<@NotNull TableData> decoded = fileCodec.decode(file.getInputStream());
        TableData tableData = decoded.stream()
                .filter(td -> td.tableName().equals(entityName))
                .findFirst()
                .orElseGet(decoded::getFirst);
        entityMappers.get(entityType.beanName()).persist(tableData);
    }

    @Override
    public Exported export(@NotNull List<String> entitiesNames, String format) throws IOException {
        FileCodec fileCodec = codecs.get(FileCodec.Type.forCodecName(format).beanName());
        List<TableData> tableDataList = new ArrayList<>();
        for (String e : entitiesNames) {
            EntityTableAdapter tableMapper = entityMappers.get(EntityTableAdapter.Type.forEntity(e).beanName());
            tableDataList.add(tableMapper.fetch());
        }
        return new Exported(fileCodec.encode(tableDataList), fileCodec.getType());
    }
}
