package io.github.two_rk_dev.pointeurback.importService.JsonImport;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.two_rk_dev.pointeurback.dto.CreateTeacherDTO;
import io.github.two_rk_dev.pointeurback.importService.FileImport;
import io.github.two_rk_dev.pointeurback.service.implementation.TeacherServiceImpl;

public class TeacherJsonImport implements FileImport {
  private final TeacherServiceImpl teacherServiceImpl;
  private final ObjectMapper mapper;

  public TeacherJsonImport(TeacherServiceImpl teacherServiceImpl, ObjectMapper mapper) {
    this.teacherServiceImpl = teacherServiceImpl;
    this.mapper = mapper;
  }

  @Override
  public boolean supports(String entityType, String FileExtension) {
    return entityType.equalsIgnoreCase("Teacher") && FileExtension.equalsIgnoreCase("json");
  }

  @Override
  public void importData(MultipartFile file) {
    try {
      CreateTeacherDTO[] teachers = mapper.readValue(file.getInputStream(), CreateTeacherDTO[].class );
      teacherServiceImpl.saveTeachers(teachers);
    } catch (IOException e) {
      throw new RuntimeException("Failed to import teacher data: " + e.getMessage());
    }
  }

}
