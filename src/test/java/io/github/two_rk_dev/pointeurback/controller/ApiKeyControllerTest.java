package io.github.two_rk_dev.pointeurback.controller;

import com.jayway.jsonpath.JsonPath;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Testcontainers
class ApiKeyControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void nonSuperadminCannotCreateApiKey() throws Exception {
        mockMvc.perform(post("/api/v1/api-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void nonSuperadminCannotListApiKeys() throws Exception {
        mockMvc.perform(get("/api/v1/api-keys"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void nonSuperadminCannotDeleteApiKeys() throws Exception {
        mockMvc.perform(delete("/api/v1/api-keys/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createdApiKeyIsUsable() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/api-keys")
                        .with(superadminUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"api key\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("api key"))
                .andExpect(jsonPath("$.rawToken").isString())
                .andExpect(jsonPath("$.createdAt").isString())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.prefix").isString())
                .andReturn();
        String apiKey = JsonPath.compile("$.rawToken").read(result.getResponse().getContentAsString());
        mockMvc.perform(get("/integration/rooms")
                        .header("X-Api-Key", apiKey))
                .andExpect(status().isOk());
    }

    @Test
    void accessingNonIntegrationApiWithApiKeyIsForbidden() throws Exception {
        ApiKeyResult apiKey = createApiKey();
        mockMvc.perform(get("/api/v1/rooms")
                        .header("X-Api-Key", apiKey))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/api-keys")
                        .header("X-Api-Key", apiKey))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletingARoomWithIntegrationApiKeyIsForbidden() throws Exception {
        ApiKeyResult apiKey = createApiKey();
        mockMvc.perform(delete("/api/v1/rooms/1")
                        .header("X-Api-Key", apiKey))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/api-keys/1")
                        .header("X-Api-Key", apiKey))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllDoesNotReturnRawKey() throws Exception {
        createApiKey();
        createApiKey();
        mockMvc.perform(get("/api/v1/api-keys")
                        .with(superadminUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawToken").doesNotExist())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("api key"))
                .andExpect(jsonPath("$[0].prefix").isString())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].createdAt").isString());
    }

    @Test
    void deletedApiKeyIsUnusable() throws Exception {
        ApiKeyResult apiKey = createApiKey();
        mockMvc.perform(delete("/api/v1/api-keys/%d".formatted(apiKey.id))
                        .with(superadminUser()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/integration/rooms")
                        .header("X-Api-Key", apiKey))
                .andExpect(status().isUnauthorized());
    }

    private ApiKeyResult createApiKey() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/api-keys")
                        .with(superadminUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"api key\"}"))
                .andExpect(status().isOk())
                .andReturn();
        Long apiKeyId = JsonPath.compile("$.id").read(result.getResponse().getContentAsString());
        String apiKey = JsonPath.compile("$.rawToken").read(result.getResponse().getContentAsString());
        return new ApiKeyResult(apiKey, apiKeyId);
    }

    private static @NotNull RequestPostProcessor superadminUser() {
        return request -> {
            request.addUserRole("SUPERADMIN");
            return request;
        };
    }

    private record ApiKeyResult(String rawToken, Long id) {
    }
}