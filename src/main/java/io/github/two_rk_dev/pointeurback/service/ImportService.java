package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.datasync.ImportMapping;
import io.github.two_rk_dev.pointeurback.dto.datasync.ImportResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
    ImportResponse batchImport(MultipartFile[] file, ImportMapping mapping, boolean ignoreConflicts);
}
