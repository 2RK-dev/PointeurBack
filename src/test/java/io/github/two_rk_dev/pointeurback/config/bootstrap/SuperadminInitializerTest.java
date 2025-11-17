package io.github.two_rk_dev.pointeurback.config.bootstrap;

import io.github.two_rk_dev.pointeurback.model.User;
import io.github.two_rk_dev.pointeurback.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SuperadminInitializerTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SuperadminInitializer superAdminInitializer;

    @Test
    void initializesSuperAdminOnFreshStartUp() {
        assertThat(userRepository.findByUsername("superadmin"))
                .as("The superadmin should have been created")
                .isPresent()
                .get()
                .as("The superadmin has the SUPERADMIN role")
                .extracting("role")
                .isEqualTo("SUPERADMIN");
    }

    @Test
    void initializationDoesNotRunIfSuperAdminAlreadyExists() {
        Optional<User> optionalUser = userRepository.findByUsername("superadmin");
        assertThat(optionalUser)
                .as("The superadmin should have been created")
                .isPresent();
        User superadmin = optionalUser.get();
        superAdminInitializer.run(null);
        assertThat(userRepository.count())
                .as("No additional user should have been created")
                .isEqualTo(1);
        assertThat(userRepository.findByUsername("superadmin"))
                .as("The superadmin should have been left unchanged")
                .isPresent()
                .get()
                .isEqualTo(superadmin);
    }
}