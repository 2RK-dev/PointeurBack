package io.github.two_rk_dev.pointeurback.controller;

import org.junit.jupiter.api.Nested;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("seed")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
@Testcontainers
class LevelControllerTest {

    @SuppressWarnings("resource")
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withReuse(true);

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createLevel_withNullBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/levels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createLevel_withEmptyBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/levels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateLevel_withNullBody_shouldReturn400() throws Exception {
        mockMvc.perform(put("/levels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateLevel_withEmptyBody_shouldReturn400() throws Exception {
        mockMvc.perform(put("/levels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createGroup_withNullBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/levels/1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createGroup_withEmptyBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/levels/1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Nested
    class ValidationTests {
        @Test
        void createLevel_withoutName_shouldReturn400() throws Exception {
            mockMvc.perform(post("/levels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"abbreviation\": \"M1\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createLevel_withoutAbbreviation_shouldReturn400() throws Exception {
            mockMvc.perform(post("/levels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"M1\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createGroup_withoutNameNorSize_shouldReturn400() throws Exception {
            mockMvc.perform(post("/levels/1/groups")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"type\": \"\", \"classe\": \"GB\"}"))
                    .andExpect(status().isBadRequest());
        }
    }
}

