package io.github.two_rk_dev.pointeurback.importService.JsonImport;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.two_rk_dev.pointeurback.dto.CreateLevelDTO;
import io.github.two_rk_dev.pointeurback.importService.FileImport;
import io.github.two_rk_dev.pointeurback.service.implementation.LevelServiceImpl;

public class LevelJsonImport implements FileImport {
  private final LevelServiceImpl levelServiceImpl;
  private final ObjectMapper mapper;

  public LevelJsonImport(LevelServiceImpl levelServiceImpl, ObjectMapper mapper) {
    this.levelServiceImpl = levelServiceImpl;
    this.mapper = mapper;
  }

  @Override
  public boolean supports(String entityType, String FileExtension) {
    return entityType.equalsIgnoreCase("Level") && FileExtension.equalsIgnoreCase("json");
  }

  @Override
  public void importData(MultipartFile file) {
    try {
      CreateLevelDTO[] levels = mapper.readValue(file.getInputStream(), CreateLevelDTO[].class );
      if (levels != null) {
        for (CreateLevelDTO dto : levels) {
          levelServiceImpl.createLevel(dto);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to import level data: " + e.getMessage());
    }
  }

}
