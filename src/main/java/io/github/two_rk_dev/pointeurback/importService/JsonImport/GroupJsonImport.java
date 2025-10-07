package io.github.two_rk_dev.pointeurback.importService.JsonImport;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.two_rk_dev.pointeurback.dto.CreateGroupDTO;
import io.github.two_rk_dev.pointeurback.importService.FileImport;
import io.github.two_rk_dev.pointeurback.service.implementation.GroupServiceImpl;

public class GroupJsonImport implements FileImport {
  private final GroupServiceImpl groupServiceImpl;
  private final ObjectMapper mapper;

  public GroupJsonImport(GroupServiceImpl groupServiceImpl, ObjectMapper mapper) {
    this.groupServiceImpl = groupServiceImpl;
    this.mapper = mapper;
  }

  @Override
  public boolean supports(String entityType, String FileExtension) {
    return entityType.equalsIgnoreCase("Group") && FileExtension.equalsIgnoreCase("json");
  }

  @Override
  public void importData(MultipartFile file) {
    try {
      CreateGroupDTO[] groups = mapper.readValue(file.getInputStream(), CreateGroupDTO[].class );
      groupServiceImpl.saveGroups(null, groups);
    } catch (IOException e) {
      throw new RuntimeException("Failed to import group data: " + e.getMessage());
    }
  }

}
