package io.github.two_rk_dev.pointeurback;

import io.github.two_rk_dev.pointeurback.datasync.filecodec.FileCodec;
import io.github.two_rk_dev.pointeurback.datasync.mapper.EntityTableMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointeurBackApplicationTests {

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
        assertThat(EntityTableMapper.Type.values())
                .as("Some entity mappers registered in the EntityTableMapper.Type enum are not implemented in the" +
                    "appropriately named spring beans.")
                .allSatisfy(fc -> applicationContext.getBean(fc.beanName(), EntityTableMapper.class));
    }
}
