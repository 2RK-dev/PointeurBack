package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.datasync.filecodec.FileCodec;
import io.github.two_rk_dev.pointeurback.repository.*;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Testcontainers
class ImportControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private TeachingUnitRepository teachingUnitRepository;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();
        levelRepository.deleteAll();
        teacherRepository.deleteAll();
        teachingUnitRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    void shouldImportCsvFilesSuccessfully() throws Exception {
        int roomCount = 2;
        int levelCount = 3;
        int teacherCount = 6;
        int groupCount = 4;
        int teachingUnitCount = 10;
        mockMvc.perform(multipart("/import/upload")
                        .file(getCsvMetadataFile())
                        .file(getCsvMultipartFile("room.csv"))
                        .file(getCsvMultipartFile("level.csv"))
                        .file(getCsvMultipartFile("group.csv"))
                        .file(getCsvMultipartFile("teaching_unit.csv"))
                        .file(getCsvMultipartFile("teacher.csv")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entitySummary.room").value(roomCount))
                .andExpect(jsonPath("$.entitySummary.level").value(levelCount))
                .andExpect(jsonPath("$.entitySummary.group").value(groupCount))
                .andExpect(jsonPath("$.entitySummary.teaching_unit").value(teachingUnitCount))
                .andExpect(jsonPath("$.entitySummary.teacher").value(teacherCount));

        assertThat(roomRepository.findAll())
                .hasSize(roomCount)
                .extracting("name")
                .containsExactlyInAnyOrder("S001", "S002");
        assertThat(levelRepository.findAll())
                .hasSize(levelCount)
                .extracting("abbreviation")
                .containsExactlyInAnyOrder("L1", "L2", "L3");
        assertThat(teacherRepository.findAll())
                .hasSize(teacherCount)
                .extracting("name")
                .contains("Alix", "Andry", "Angelo", "Bertin", "Cyprien", "Jean Christian RALAIVAO");
        assertThat(groupRepository.findAll())
                .hasSize(groupCount)
                .extracting("name")
                .containsExactlyInAnyOrder("L1Gp1", "L1Gp2", "L2Gp1", "L2Gp2");
        assertThat(teachingUnitRepository.findAll())
                .hasSize(teachingUnitCount)
                .extracting("abbreviation")
                .containsExactlyInAnyOrder("PROG", "BDD", "MATH", "TRES", "LANG", "MATH", "PROG", "BDD", "TRES", "LANG");
    }

    @Test
    void shouldImportExcelFileSuccessfully() throws Exception {
        MockMultipartFile excelFile = new MockMultipartFile(
                "files",
                "level_room_teacher_teaching_unit_group.xlsx",
                FileCodec.Type.EXCEL.inputMediaType().toString(),
                new ClassPathResource("level_room_teacher_teaching_unit_group.xlsx").getInputStream()
        );
        int roomCount = 6;
        int levelCount = 3;
        int groupCount = 4;
        int teachingUnitCount = 10;
        int teacherCount = 6;
        mockMvc.perform(multipart("/import/upload")
                        .file(getExcelMetadataFile())
                        .file(excelFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entitySummary.room").value(roomCount))
                .andExpect(jsonPath("$.entitySummary.level").value(levelCount))
                .andExpect(jsonPath("$.entitySummary.group").value(groupCount))
                .andExpect(jsonPath("$.entitySummary.teaching_unit").value(teachingUnitCount))
                .andExpect(jsonPath("$.entitySummary.teacher").value(teacherCount));

        assertThat(roomRepository.findAll())
                .hasSize(roomCount)
                .extracting("name")
                .containsExactlyInAnyOrder("S001", "S002", "S003", "S101", "S001", "S002");
        assertThat(levelRepository.findAll())
                .hasSize(levelCount)
                .extracting("abbreviation")
                .containsExactlyInAnyOrder("L1", "L2", "L3");
        assertThat(teacherRepository.findAll())
                .hasSize(teacherCount)
                .extracting("name")
                .contains("Alix", "Andry", "Angelo", "Bertin", "Cyprien", "Jean Christian RALAIVAO");
        assertThat(groupRepository.findAll())
                .hasSize(groupCount)
                .extracting("name")
                .containsExactlyInAnyOrder("L1Gp1", "L1Gp2", "L2Gp1", "L2Gp2");
        assertThat(teachingUnitRepository.findAll())
                .hasSize(teachingUnitCount)
                .extracting("abbreviation")
                .containsExactlyInAnyOrder("PROG", "BDD", "MATH", "TRES", "LANG", "MATH", "PROG", "BDD", "TRES", "LANG");
    }

    @Test
    void shouldImportJSONFileSuccessfully() throws Exception {
        int roomCount = 6;
        int levelCount = 3;
        int teacherCount = 6;
        int groupCount = 4;
        int teachingUnitCount = 10;
        MockMultipartFile jsonFile = new MockMultipartFile(
                "files",
                "level_room_teacher_teaching_unit_group.json",
                "application/json",
                new ClassPathResource("level_room_teacher_teaching_unit_group.json").getInputStream()
        );
        mockMvc.perform(multipart("/import/upload")
                        .file(getJSONMetadataFile())
                        .file(jsonFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entitySummary.room").value(roomCount))
                .andExpect(jsonPath("$.entitySummary.level").value(levelCount))
                .andExpect(jsonPath("$.entitySummary.group").value(groupCount))
                .andExpect(jsonPath("$.entitySummary.teaching_unit").value(teachingUnitCount))
                .andExpect(jsonPath("$.entitySummary.teacher").value(teacherCount));

        assertThat(roomRepository.findAll())
                .hasSize(roomCount)
                .extracting("name")
                .containsExactlyInAnyOrder("S001", "S002", "S003", "S101", "S001", "S002");
        assertThat(levelRepository.findAll())
                .hasSize(levelCount)
                .extracting("abbreviation")
                .containsExactlyInAnyOrder("L1", "L2", "L3");
        assertThat(teacherRepository.findAll())
                .hasSize(teacherCount)
                .extracting("name")
                .contains("Alix", "Andry", "Angelo", "Bertin", "Cyprien", "Jean Christian RALAIVAO");
        assertThat(groupRepository.findAll())
                .hasSize(groupCount)
                .extracting("name")
                .containsExactlyInAnyOrder("L1Gp1", "L1Gp2", "L2Gp1", "L2Gp2");
        assertThat(teachingUnitRepository.findAll())
                .hasSize(teachingUnitCount)
                .extracting("abbreviation")
                .containsExactlyInAnyOrder("PROG", "BDD", "MATH", "TRES", "LANG", "MATH", "PROG", "BDD", "TRES", "LANG");
    }

    private @NotNull MockMultipartFile getCsvMultipartFile(String filename) {
        try {
            return new MockMultipartFile(
                    "files",
                    filename,
                    "text/csv",
                    new ClassPathResource(filename).getInputStream()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull MockMultipartFile getCsvMetadataFile() {
        @Language("JSON") String metadata = """
                {
                  "metadata": {
                    "room.csv": {
                      "": {
                        "entityType": "room",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation",
                          "size": "size"
                        }
                      }
                    },
                    "level.csv": {
                      "": {
                        "entityType": "level",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation"
                        }
                      }
                    },
                    "teacher.csv": {
                      "": {
                        "entityType": "teacher",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation"
                        }
                      }
                    },
                    "group.csv": {
                      "": {
                        "entityType": "group",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation",
                          "size": "size",
                          "levelId": "levelId"
                        }
                      }
                    },
                    "teaching_unit.csv": {
                      "": {
                        "entityType": "teaching_unit",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation",
                          "levelId": "levelId"
                        }
                      }
                    }
                  }
                }
                """;

        return new MockMultipartFile(
                "metadata",
                "",
                "application/json",
                metadata.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static @NotNull MockMultipartFile getExcelMetadataFile() {
        @Language("JSON") String metadata = """
                {
                  "metadata": {
                    "level_room_teacher_teaching_unit_group.xlsx": {
                      "room": {
                        "entityType": "room",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation",
                          "size": "size"
                        }
                      },
                      "level": {
                        "entityType": "level",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation"
                        }
                      },
                      "teacher": {
                        "entityType": "teacher",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation"
                        }
                      },
                      "group": {
                        "entityType": "group",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation",
                          "size": "size",
                          "levelId": "levelId"
                        }
                      },
                      "teaching_unit": {
                        "entityType": "teaching_unit",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation",
                          "levelId": "levelId"
                        }
                      }
                    }
                  }
                }
                """;

        return new MockMultipartFile(
                "metadata",
                "",
                "application/json",
                metadata.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static @NotNull MockMultipartFile getJSONMetadataFile() {
        @Language("JSON") String metadata = """
                {
                  "metadata": {
                    "level_room_teacher_teaching_unit_group.json": {
                      "room": {
                        "entityType": "room",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation",
                          "size": "size"
                        }
                      },
                      "level": {
                        "entityType": "level",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation"
                        }
                      },
                      "teacher": {
                        "entityType": "teacher",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation"
                        }
                      },
                      "group": {
                        "entityType": "group",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation",
                          "size": "size",
                          "levelId": "levelId"
                        }
                      },
                      "teaching_unit": {
                        "entityType": "teaching_unit",
                        "headersMapping": {
                          "id": "id",
                          "name": "name",
                          "abbreviation": "abbreviation",
                          "levelId": "levelId"
                        }
                      }
                    }
                  }
                }
                """;

        return new MockMultipartFile(
                "metadata",
                "",
                "application/json",
                metadata.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void testPostgresContainerIsRunning() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(postgres.getDatabaseName()).isEqualTo("test");
    }
}

