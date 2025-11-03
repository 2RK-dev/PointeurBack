package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.datasync.ImportMapping;
import io.github.two_rk_dev.pointeurback.dto.datasync.SyncError;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImportService {
    List<SyncError> batchImport(MultipartFile[] file, ImportMapping mapping);
}
