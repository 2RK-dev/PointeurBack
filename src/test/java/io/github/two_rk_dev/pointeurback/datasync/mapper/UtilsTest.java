package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UtilsTest {

    private final Map<String, ColumnFieldBinding<ExampleDTO>> BINDINGS = Utils.buildMapping(ExampleDTO.class);

    @Test
    void parseDTOSkipColumnsWithNullHeaders() {
        Optional<ExampleDTO> dto = Utils.parseDTO(
                List.of("false", "1", "false", "John Doe", "Golf", "18"),
                List.of("married", "id", "isAdmin", "name", "hobby", "age"),
                ExampleDTO.class,
                BINDINGS,
                new ObjectMapper()
        );
        assertThat(dto).isNotEmpty();
        assertThat(dto.get()).isEqualTo(new ExampleDTO(1L, "John Doe", 18, false));
    }

    @Test
    void parseDTOAutoFillAbsentHeadersWithNull() {
        Optional<ExampleDTO> dto = Utils.parseDTO(
                List.of("false", "John Doe", "Golf", "18"),
                List.of("married", "name", "hobby", "age"),
                ExampleDTO.class,
                BINDINGS,
                new ObjectMapper()
        );
        assertThat(dto).isNotEmpty();
        assertThat(dto.get()).isEqualTo(new ExampleDTO(null, "John Doe", 18, null));
    }

    record ExampleDTO(Long id, String name, int age, Boolean isAdmin) {
    }
}