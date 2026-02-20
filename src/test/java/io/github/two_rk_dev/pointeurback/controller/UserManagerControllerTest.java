package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.config.AuthProperties;
import io.github.two_rk_dev.pointeurback.dto.*;
import io.github.two_rk_dev.pointeurback.model.User;
import io.github.two_rk_dev.pointeurback.repository.UserRepository;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.assertj.core.api.SoftAssertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(RestTemplateTestConfig.class)
@ActiveProfiles("test")
class UserManagerControllerTest extends AbstractRandomPortSpringBootTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private CookieStore cookieStore;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthProperties authProperties;
    private String superadminUsername;
    private String superadminPassword;

    @BeforeEach
    void setUp() {
        cookieStore.clear();
        userRepository.deleteAllByRoleIsNot("SUPERADMIN");
        superadminUsername = authProperties.bootstrapSuperadmin().username();
        superadminPassword = authProperties.bootstrapSuperadmin().password();
    }

    @Override
    protected int getPort() {
        return port;
    }

    private ResponseEntity<LoginResponseDTO> login(String username, String password) {
        String requestBody = "{\"username\":\"%s\", \"password\":\"%s\"}".formatted(username, password);
        return testRestTemplate.postForEntity(url("/auth/login"), requestBody, LoginResponseDTO.class);
    }

    private @NotNull User createNewAdmin(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ADMIN");
        return userRepository.save(user);
    }

    @Nested
    class CreateTests {
        @Test
        void successfullyCreateUser() {
            String accessToken = Objects.requireNonNull(login(superadminUsername, superadminPassword).getBody()).accessToken();
            ResponseEntity<UserCreatedDTO> response = testRestTemplate.postForEntity(
                    url("/users"),
                    createHttpEntity("{\"username\": \"8user_4\"}", accessToken),
                    UserCreatedDTO.class
            );
            assertThat(response)
                    .as("Should 201 successfully create user")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody())
                    .as("Created user should have the correct username")
                    .isNotNull()
                    .extracting(UserCreatedDTO::info)
                    .extracting(UserInfoDTO::username)
                    .isEqualTo("8user_4");
            assertThat(userRepository.count())
                    .as("The number of users should increase by one")
                    .isEqualTo(2);
            ResponseEntity<String> loginResponse = testRestTemplate.postForEntity(
                    url("/auth/login"),
                    new LoginRequestDTO("8user_4", response.getBody().password()),
                    String.class
            );
            assertThat(loginResponse)
                    .as("Should be able to login with the newly-created user")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.OK);
        }

        @Test
        void invalidRequestBody_returns400() {
            String accessToken = Objects.requireNonNull(login(superadminUsername, superadminPassword).getBody()).accessToken();
            SoftAssertions softly = new SoftAssertions();
            List.of(
                    "{\"username\": \"usérname\"}",
                    "{\"username\": \"\"}",
                    "{\"username\": \"user&\"}",
                    "{}"
            ).forEach(req -> softly.assertThat(testRestTemplate.postForEntity(
                                            url("/users"),
                                            createHttpEntity(req, accessToken),
                                            String.class
                                    )
                            )
                            .as("Create user with body " + req + " should be 400")
                            .extracting(ResponseEntity::getStatusCode)
                            .isEqualTo(HttpStatus.BAD_REQUEST)
            );
            softly.assertAll();
        }

        @Test
        void withIncorrectRole_returns403() {
            createNewAdmin("admin", "password");
            String accessToken = Objects.requireNonNull(login("admin", "password").getBody()).accessToken();
            ResponseEntity<String> response = testRestTemplate.postForEntity(
                    url("/users"),
                    createHttpEntity("{\"username\": \"8user_4\"}", accessToken),
                    String.class
            );
            assertThat(response)
                    .as("Non superadmins should be 403 forbidden from creating users")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.FORBIDDEN);
        }
    }

    @Nested
    class ReadTests {
        @Test
        void successful_excludeTheSuperadmin() {
            String accessToken = Objects.requireNonNull(login(superadminUsername, superadminPassword).getBody()).accessToken();
            User admin = createNewAdmin("admin", "password");
            ResponseEntity<List<UserDTO>> response = testRestTemplate.exchange(
                    url("/users"),
                    HttpMethod.GET,
                    createHttpEntity(null, accessToken),
                    new ParameterizedTypeReference<>() {
                    }
            );
            assertThat(response)
                    .as("[Get All] Should 200 OK fetch the users")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.OK);
            assertThat(response.getBody())
                    .as("[Get All] Users list returned should not contain the superadmin")
                    .isNotNull()
                    .filteredOn(user -> superadminUsername.equals(user.info().username()))
                    .isEmpty();
            assertThat(response.getBody())
                    .as("[Get All] Users list returned should contain the created user")
                    .hasSize(1)
                    .first()
                    .extracting(UserDTO::info)
                    .isEqualTo(new UserInfoDTO("admin", "ADMIN"));

            ResponseEntity<UserDTO> getOneResponse = testRestTemplate.exchange(
                    url("/users/" + admin.getId()),
                    HttpMethod.GET,
                    createHttpEntity(null, accessToken),
                    UserDTO.class
            );
            assertThat(getOneResponse)
                    .as("[Get One] Should 200 OK fetch the user")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.OK);
            assertThat(getOneResponse.getBody())
                    .as("[Get One] Should contain the correct user")
                    .isEqualTo(new UserDTO(admin.getId(), new UserInfoDTO("admin", "ADMIN")));
        }

        @Test
        void withIncorrectRole_returns403() {
            User admin = createNewAdmin("an-admin", "random");
            String accessToken = Objects.requireNonNull(login("an-admin", "random").getBody()).accessToken();
            ResponseEntity<Object> response = testRestTemplate.exchange(
                    url("/users"),
                    HttpMethod.GET,
                    createHttpEntity(null, accessToken),
                    new ParameterizedTypeReference<>() {
                    }
            );
            assertThat(response)
                    .as("[Get All] Non superadmins should be 403 forbidden from getting users")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.FORBIDDEN);

            ResponseEntity<UserDTO> getOneResponse = testRestTemplate.exchange(
                    url("/users/" + admin.getId()),
                    HttpMethod.GET,
                    createHttpEntity(null, accessToken),
                    UserDTO.class
            );
            assertThat(getOneResponse)
                    .as("[Get One] Non superadmins should be 403 forbidden from getting users")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        void getSuperadmin_returnsNotFound() {
            String accessToken = Objects.requireNonNull(login(superadminUsername, superadminPassword).getBody()).accessToken();
            Long superadminId = userRepository.findByUsername(superadminUsername).orElseThrow().getId();
            ResponseEntity<UserDTO> response = testRestTemplate.exchange(
                    url("/users/" + superadminId),
                    HttpMethod.GET,
                    createHttpEntity(null, accessToken),
                    UserDTO.class
            );
            assertThat(response)
                    .as("[Get One] Superadmin should not be fetchable using its ID")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void successful_existingUser_isDeleted204() {
            String accessToken = Objects.requireNonNull(login(superadminUsername, superadminPassword).getBody()).accessToken();
            User admin = createNewAdmin("admin", "admin");
            ResponseEntity<Void> response = testRestTemplate.exchange(
                    url("/users/" + admin.getId()),
                    HttpMethod.DELETE,
                    createHttpEntity(null, accessToken),
                    Void.class
            );
            assertThat(userRepository.count())
                    .as("The user should be deleted")
                    .isEqualTo(1);
            assertThat(response)
                    .as("The app should return 204")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        void successful_nonExistingUser_isStill204() {
            String accessToken = Objects.requireNonNull(login(superadminUsername, superadminPassword).getBody()).accessToken();
            long nonExistentUserId = createNewAdmin("admin", "admin").getId() + 200;
            ResponseEntity<Void> response = testRestTemplate.exchange(
                    url("/users/" + nonExistentUserId),
                    HttpMethod.DELETE,
                    createHttpEntity(null, accessToken),
                    Void.class
            );
            assertThat(userRepository.count())
                    .as("No deletion should happen")
                    .isEqualTo(2);
            assertThat(response)
                    .as("The app should return 204")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        void successful_superadmin_isNeverDeleted204() {
            String accessToken = Objects.requireNonNull(login(superadminUsername, superadminPassword).getBody()).accessToken();
            long superadminId = userRepository.findByUsername(superadminUsername).orElseThrow().getId();
            ResponseEntity<Void> response = testRestTemplate.exchange(
                    url("/users/" + superadminId),
                    HttpMethod.DELETE,
                    createHttpEntity(null, accessToken),
                    Void.class
            );
            assertThat(userRepository.count())
                    .as("No user should have been deleted")
                    .isEqualTo(1);
            assertThat(response)
                    .as("The app should return 204")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        void forbidden_whenIncorrectRole_returns403() {
            createNewAdmin("admin", "random");
            String accessToken = Objects.requireNonNull(login("admin", "random").getBody()).accessToken();
            ResponseEntity<Void> response = testRestTemplate.exchange(
                    url("/users/" + 800),
                    HttpMethod.DELETE,
                    createHttpEntity(null, accessToken),
                    Void.class
            );
            assertThat(response)
                    .as("The app should return 403 forbidden")
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.FORBIDDEN);
        }
    }
}