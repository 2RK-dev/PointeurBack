package io.github.two_rk_dev.pointeurback.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Testcontainers
class TeachingUnitControllerTest {

    @SuppressWarnings("resource")
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withReuse(true);

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createTeachingUnit_withNullBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/teachingUnits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTeachingUnit_withEmptyBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/teachingUnits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTeachingUnit_withNullBody_shouldReturn400() throws Exception {
        mockMvc.perform(put("/teachingUnits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTeachingUnit_withEmptyBody_shouldReturn400() throws Exception {
        mockMvc.perform(put("/teachingUnits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Nested
    class ValidationTests {
        @Test
        void createTeachingUnit_withoutName_shouldReturn400() throws Exception {
            mockMvc.perform(post("/teachingUnits")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"abbreviation\": \"LANG\"}"))
                    .andExpect(status().isBadRequest());
        }
    }
}
