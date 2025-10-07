package io.github.two_rk_dev.pointeurback.importService;

import org.springframework.web.multipart.MultipartFile;

public interface FileImport {
  boolean supports(String entityType, String fileExtension);

  void importData(MultipartFile file);
}
