package io.github.two_rk_dev.pointeurback;

import io.github.two_rk_dev.pointeurback.datasync.filecodec.FileCodec;
import io.github.two_rk_dev.pointeurback.datasync.mapper.EntityTableAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class PointeurBackApplicationTests {

    @SuppressWarnings("resource")
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withReuse(true);

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
    }

    @Test
    void allRegisteredFileCodecEnumsAreImplemented() {
        assertThat(FileCodec.Type.values())
                .as("Some file codecs registered in the FileCodec.Type enum are not implemented in the" +
                    "appropriately named spring beans.")
                .allSatisfy(fc -> assertThat(applicationContext.getBean(fc.beanName(), FileCodec.class).getType()).isEqualTo(fc));
    }

    @Test
    void allRegisteredEntityTableMapperEnumsAreImplemented() {
        assertThat(EntityTableAdapter.Type.values())
                .as("Some entity mappers registered in the EntityTableMapper.Type enum are not implemented in the" +
                    "appropriately named spring beans.")
                .allSatisfy(fc -> applicationContext.getBean(fc.beanName(), EntityTableAdapter.class));
    }
}
