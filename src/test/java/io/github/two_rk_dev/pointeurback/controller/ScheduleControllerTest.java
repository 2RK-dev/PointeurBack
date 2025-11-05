package io.github.two_rk_dev.pointeurback.controller;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Testcontainers
@ActiveProfiles("seed")
@Transactional
class ScheduleControllerTest {

    @SuppressWarnings("resource")
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withReuse(true);

    @Autowired
    private MockMvc mockMvc;

    @Test
    void batchScheduleItemCreate_returns200() throws Exception {
        @Language("JSON") String requestBody = """
                [
                  {
                    "groupIds": [1, 2],
                    "teacherId": 1,
                    "teachingUnitId": 1,
                    "roomId": 1,
                    "startTime": "2025-11-05T12:00:09.716Z",
                    "endTime": "2025-11-05T14:00:09.716Z"
                  },
                  {
                    "groupIds": [3, 4],
                    "teacherId": 2,
                    "teachingUnitId": 3,
                    "roomId": 2,
                    "startTime": "2025-11-05T12:00:09.716Z",
                    "endTime": "2025-11-05T14:00:09.716Z"
                  },
                  {
                    "groupIds": [3],
                    "teacherId": 1,
                    "teachingUnitId": 1,
                    "roomId": 2,
                    "startTime": "2025-11-06T15:30:09.716Z",
                    "endTime": "2025-11-06T16:00:09.716Z"
                  }
                ]
                """;
        mockMvc.perform(post("/schedule/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successItems", hasSize(3)))
                .andExpect(jsonPath("$.failedItems", hasSize(0)));
    }

    @Test
    void batchScheduleCreate_withSomeInvalidItems_stillReturns200() throws Exception {
        @Language("JSON") String requestBody = """
                [
                  {
                    "groupIds": [1, 2],
                    "teacherId": 1,
                    "teachingUnitId": 1,
                    "roomId": 1,
                    "startTime": "2025-11-05T12:00:09.716Z",
                    "endTime": "2025-11-05T14:00:09.716Z"
                  },
                  {
                    "groupIds": [3, 4],
                    "teacherId": 2,
                    "teachingUnitId": 3000,
                    "roomId": 2,
                    "startTime": "2025-11-05T12:00:09.716Z",
                    "endTime": "2025-11-05T14:00:09.716Z"
                  }
                ]
                """;
        mockMvc.perform(post("/schedule/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successItems", hasSize(1)))
                .andExpect(jsonPath("$.failedItems", hasSize(1)));
    }

    @Test
    void batchScheduleCreate_withNoValidItem_stillReturns200() throws Exception {
        @Language("JSON") String requestBody = """
                [
                  {
                    "groupIds": [1, 2],
                    "teacherId": 1,
                    "teachingUnitId": 100000,
                    "roomId": 100,
                    "startTime": "2025-11-05T12:00:09.716Z",
                    "endTime": "2025-11-05T14:00:09.716Z"
                  },
                  {
                    "groupIds": [3, 4],
                    "teacherId": 2,
                    "teachingUnitId": 1,
                    "roomId": 1,
                    "startTime": "2025-11-05T15:00:09.716Z",
                    "endTime": "2025-11-05T14:00:09.716Z"
                  }
                ]
                """;
        mockMvc.perform(post("/schedule/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successItems", hasSize(0)))
                .andExpect(jsonPath("$.failedItems", hasSize(2)));
    }

    @Test
    void addScheduleItem_withNullBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addScheduleItem_withEmptyBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSchedule_withNullBody_shouldReturn400() throws Exception {
        mockMvc.perform(put("/schedule/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSchedule_withEmptyBody_shouldReturn400() throws Exception {
        mockMvc.perform(put("/schedule/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }
}

