package io.github.two_rk_dev.pointeurback.importService.JsonImport;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.two_rk_dev.pointeurback.dto.CreateRoomDTO;
import io.github.two_rk_dev.pointeurback.importService.FileImport;
import io.github.two_rk_dev.pointeurback.service.implementation.RoomServiceImpl;

public class RoomJsonImport implements FileImport {
  private final RoomServiceImpl roomServiceImpl;
  private final ObjectMapper mapper;

  public RoomJsonImport(RoomServiceImpl roomServiceImpl, ObjectMapper mapper) {
    this.roomServiceImpl = roomServiceImpl;
    this.mapper = mapper;
  }

  @Override
  public boolean supports(String entityType, String FileExtension) {
    return entityType.equalsIgnoreCase("Room") && FileExtension.equalsIgnoreCase("json");
  }

  @Override
  public void importData(MultipartFile file) {
    try {
      CreateRoomDTO[] rooms = mapper.readValue(file.getInputStream(), CreateRoomDTO[].class );
      roomServiceImpl.saveRooms(rooms);
    } catch (IOException e) {
      throw new RuntimeException("Failed to import room data: " + e.getMessage());
    }
  }


}
