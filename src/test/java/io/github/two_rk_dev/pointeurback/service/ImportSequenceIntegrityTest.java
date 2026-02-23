package io.github.two_rk_dev.pointeurback.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.datasync.mapper.EntityTableAdapter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This test suite is to prevent duplicate primary keys when importing data with IDs. The data integrity violation is
 * caused by the sequences backing the IDs' auto-increment becoming "out-of-sync" and generates already existing IDs.
 */
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false, addFilters = false)
@Testcontainers
public class ImportSequenceIntegrityTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Map<String, EntityTableAdapter> entityTableAdapters;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        entityTableAdapters.forEach((name, adapter) -> {
            jdbcTemplate.execute("TRUNCATE TABLE " + adapter.getEntityType().tableName + " RESTART IDENTITY CASCADE");
            Mockito.reset(adapter);
        });
    }

    @TestFactory
    Stream<DynamicTest> allEntityTableAdapterBeans_haveSequenceIntegrityTestFiles() {
        return entityTableAdapters.keySet().stream()
                .map(beanName -> dynamicTest(
                        beanName + " should have a sequence integrity test",
                        () -> assertThat(List.of("%s.json".formatted(beanName), "__%s.json.json".formatted(beanName)))
                                .allSatisfy(fileName -> {
                                    String path = "sequence-integrity-tests/" + fileName;
                                    ClassPathResource testCase = new ClassPathResource(path);
                                    assertThat(testCase.exists())
                                            .as("The test file " + path + " should exist")
                                            .isTrue();
                                    assertThatCode(() -> objectMapper.readTree(testCase.getInputStream()))
                                            .as("The test file " + path + " should be a valid json")
                                            .doesNotThrowAnyException();
                                })
                ));
    }

    @TestFactory
    Stream<DynamicTest> afterImport_allImportableTables_sequenceAheadOfMaxId() {
        return entityTableAdapters.keySet().stream()
                .map(beanName -> dynamicTest(beanName + " should synchronize the ID sequence after import", () -> {
                    String fileName = "%s.json".formatted(beanName);
                    MockMultipartFile importFile = new MockMultipartFile(
                            "files",
                            fileName,
                            "application/json",
                            new ClassPathResource("sequence-integrity-tests/" + fileName).getInputStream()
                    );
                    mockMvc.perform(multipart("/api/v1/import/upload")
                                    .file(getMetadataFile(fileName))
                                    .file(importFile))
                            .andExpect(status().isOk());

                    EntityTableAdapter tableAdapter = entityTableAdapters.get(beanName);
                    Mockito.verify(tableAdapter, Mockito.atLeastOnce())
                            .process(Mockito.any(), Mockito.any(), Mockito.anyBoolean());
                    Mockito.verify(tableAdapter, Mockito.atLeastOnce())
                            .finalize(Mockito.any(), Mockito.anyBoolean());

                    String tableName = tableAdapter.getEntityType().tableName;
                    String pkColumn = jdbcTemplate.queryForObject("""
                            SELECT a.attname FROM pg_index i
                            JOIN pg_attribute a ON a.attrelid = i.indrelid AND a.attnum = ANY(i.indkey)
                            WHERE i.indrelid = ?::regclass AND i.indisprimary
                            """, String.class, tableName
                    );
                    Long maxId = jdbcTemplate.queryForObject(
                            "SELECT COALESCE(MAX(" + pkColumn + "), 0) FROM " + tableName,
                            Long.class
                    );
                    String sequenceName = jdbcTemplate.queryForObject("SELECT pg_get_serial_sequence(?, ?)", String.class, tableName, pkColumn);
                    Long seqVal = jdbcTemplate.queryForObject(
                            "SELECT last_value FROM %s".formatted(sequenceName),
                            Long.class
                    );
                    long diff = Objects.requireNonNull(seqVal) - Objects.requireNonNull(maxId);
                    assertThat(diff)
                            .withFailMessage("Sequence for %s is lagging by %d", tableName, diff)
                            .isGreaterThanOrEqualTo(0);
                }));
    }

    private static @NotNull MockMultipartFile getMetadataFile(String fileName) throws IOException {
        byte[] metadata = new ClassPathResource("__%s.json".formatted(fileName)).getContentAsByteArray();
        return new MockMultipartFile("metadata", "", "application/json", metadata);
    }

    @TestConfiguration
    static class EntityAdapterSpyConfig {
        @Bean
        public static BeanPostProcessor adapterSpyPostProcessor() {
            return new BeanPostProcessor() {
                @Override
                public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) {
                    if (bean instanceof EntityTableAdapter) {
                        return Mockito.spy(bean);
                    }
                    return bean;
                }
            };
        }
    }
}