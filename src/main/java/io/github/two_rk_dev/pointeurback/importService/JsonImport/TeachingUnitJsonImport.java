package io.github.two_rk_dev.pointeurback.importService.JsonImport;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.two_rk_dev.pointeurback.dto.CreateTeachingUnitDTO;
import io.github.two_rk_dev.pointeurback.importService.FileImport;
import io.github.two_rk_dev.pointeurback.service.implementation.TeachingUnitServiceImpl;

public class TeachingUnitJsonImport implements FileImport {
  private final TeachingUnitServiceImpl teachingUnitServiceImpl;
  private final ObjectMapper mapper;

  public TeachingUnitJsonImport(TeachingUnitServiceImpl teachingUnitServiceImpl, ObjectMapper mapper) {
    this.teachingUnitServiceImpl = teachingUnitServiceImpl;
    this.mapper = mapper;
  }

  @Override
  public boolean supports(String entityType, String FileExtension) {
    return entityType.equalsIgnoreCase("TeachingUnit") && FileExtension.equalsIgnoreCase("json");
  }

  @Override
  public void importData(MultipartFile file) {
    try {
      CreateTeachingUnitDTO[] units = mapper.readValue(file.getInputStream(), CreateTeachingUnitDTO[].class );
      teachingUnitServiceImpl.saveTeachingUnits(units);
    } catch (IOException e) {
      throw new RuntimeException("Failed to import teaching unit data: " + e.getMessage());
    }
  }

}
