package io.github.two_rk_dev.pointeurback.importService;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileImportContext {

    private final List<FileImport> importers;

    public FileImportContext(List<FileImport> importers) {
        this.importers = importers;
    }

    /**
     * Find a matching importer based on entityType and file extension and delegate import.
     * @param entityType entity name, e.g. "Room"
     * @param file uploaded multipart file
     */
    public void importFile(String entityType, MultipartFile file) {
        if (entityType == null || file == null) {
            throw new IllegalArgumentException("entityType and file must be provided");
        }

        String filename = file.getOriginalFilename();
        String extension = null;
        if (filename != null && filename.contains(".")) {
            extension = filename.substring(filename.lastIndexOf('.') + 1);
        }

        for (FileImport importer : importers) {
            if (importer.supports(entityType, extension)) {
                importer.importData(file);
                return;
            }
        }

        throw new UnsupportedOperationException("No importer found for entity: " + entityType + " and extension: " + extension);
    }
}

