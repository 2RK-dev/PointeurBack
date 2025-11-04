package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.datasync.ImportResponse;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportMapping;
import io.github.two_rk_dev.pointeurback.dto.datasync.SyncError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImportService {
    List<SyncError> batchImport(MultipartFile[] file, ImportMapping mapping);
    void import_(String entityType, MultipartFile file) throws IOException;

    ImportResponse importMultipleFiles(MultipartFile[] files) throws IOException;
}
