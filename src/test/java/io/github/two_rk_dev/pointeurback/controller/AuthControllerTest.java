package io.github.two_rk_dev.pointeurback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.github.two_rk_dev.pointeurback.dto.UserDTO;
import io.github.two_rk_dev.pointeurback.model.RefreshToken;
import io.github.two_rk_dev.pointeurback.model.User;
import io.github.two_rk_dev.pointeurback.repository.RefreshTokenRepository;
import io.github.two_rk_dev.pointeurback.repository.UserRepository;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.HttpCookie;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class AuthControllerTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    int port;

    final String REFRESH_TOKEN_URL = "/api/v1/auth/refresh";
    final String LOGIN_API_URL = "/api/v1/auth/login";
    final String LOGOUT_API_URL = "/api/v1/auth/logout";
    final String PROTECTED_URL = "/api/v1/auth/me";
    final String ADMIN_URL = "/api/v1/rooms";
    final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private CookieStore cookieStore;

    private static @NotNull List<HttpCookie> cookies(@NotNull ResponseEntity<?> res) {
        List<String> raw = res.getHeaders().getOrDefault(HttpHeaders.SET_COOKIE, List.of());
        return raw.stream()
                .flatMap(header -> HttpCookie.parse(header).stream())
                .toList();
    }

    private static @NotNull ClientHttpResponse contentTypeInterceptor(
            @NotNull HttpRequest request,
            byte[] body,
            @NotNull ClientHttpRequestExecution execution) throws IOException {

        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return execution.execute(request, body);
    }

    private static HttpCookie cookieValue(String name, @NotNull List<HttpCookie> cookies) {
        return cookies.stream()
                .filter(c -> Objects.equals(c.getName(), name))
                .findFirst()
                .orElse(null);
    }

    @Contract(pure = true)
    private @NotNull String url(String path) {
        return "http://localhost:" + port + path;
    }

    private ResponseEntity<String> login(String username, String password) {
        String requestBody = "{\"username\":\"%s\", \"password\":\"%s\"}".formatted(username, password);
        return testRestTemplate.postForEntity(url(LOGIN_API_URL), requestBody, String.class);
    }

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setRole("admin");
        userRepository.save(user);
        cookieStore.clear();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        refreshTokenRepository.deleteAll();
    }

    @TestConfiguration
    static class AuthControllerTestConfig {
        @Bean
        public CookieStore cookieStore() {
            return new BasicCookieStore();
        }

        @Bean
        public TestRestTemplate testRestTemplate(CookieStore cookieStore) {
            return new TestRestTemplate(new RestTemplateBuilder()
                    .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(
                            HttpClientBuilder.create()
                                    .setDefaultCookieStore(cookieStore)
                                    .build())
                    ).interceptors(AuthControllerTest::contentTypeInterceptor)
            );
        }
    }

    @Nested
    class LoginTests {

        @Test
        void successfulLogin() {
            ResponseEntity<String> response = login("admin", "admin");
            assertThat(response.getStatusCode())
                    .as("Status code is 200 success")
                    .isEqualTo(HttpStatus.OK);
            String deviceId = Objects.requireNonNull(
                    cookieValue("device_id", cookies(response)),
                    "device_id cookie should not be null"
            ).getValue();
            String body = response.getBody();
            assertThat(refreshTokenRepository.findByDeviceIdAndUser_Username(deviceId, "admin"))
                    .as("A new refresh token entry should be created in DB")
                    .isNotEmpty();
            assertThat(JsonPath.compile("$.access_token").<String>read(body))
                    .as("An access_token should be returned")
                    .isNotBlank();
            assertThat(mapper.convertValue(JsonPath.compile("$.user").read(body), UserDTO.class))
                    .as("An user should be returned")
                    .isEqualTo(new UserDTO("admin", "admin"));
            assertThat(cookieValue("refresh_token", cookies(response)))
                    .as("A refresh_token should be returned, HTTP-only")
                    .isNotNull()
                    .matches(HttpCookie::isHttpOnly);
            assertThat(cookieValue("device_id", cookies(response)))
                    .as("A device_id cookie should be set and HTTP-only")
                    .isNotNull()
                    .matches(HttpCookie::isHttpOnly);
        }

        @Test
        void invalidCredentialsReturnUnauthorized() {
            ResponseEntity<String> response = login("admon", "adman");
            assertThat(response.getStatusCode())
                    .as("Status code should be 401 unauthorized")
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @Nested
    class RefreshTokenTests {

        @Test
        void refresh_success_returns200AndNewAccessToken() throws Exception {
            ResponseEntity<String> loginResponse = login("admin", "admin");
            assertThat(cookieValue("refresh_token", cookies(loginResponse)))
                    .as("refresh_token cookie should not be null after login")
                    .isNotNull();

            ResponseEntity<String> refreshResponse = testRestTemplate.postForEntity(
                    url(REFRESH_TOKEN_URL),
                    null,
                    String.class
            );

            assertThat(refreshResponse.getStatusCode())
                    .as("Status code should be 200 success")
                    .isEqualTo(HttpStatus.OK);
            assertThat(mapper.readTree(refreshResponse.getBody()).get("access_token"))
                    .as("A new access_token should be returned")
                    .isNotNull();
        }

        @Test
        void refresh_expiredToken_returns401() {
            String deviceId = UUID.randomUUID().toString();
            RefreshToken rt = new RefreshToken();
            rt.setDeviceId(deviceId);
            rt.setUser(userRepository.findByUsername("admin").orElse(null));
            rt.setToken(UUID.randomUUID().toString());
            rt.setExpiresAt(OffsetDateTime.now().minusHours(1));
            rt.setCreatedAt(OffsetDateTime.now());
            refreshTokenRepository.save(rt);
            HttpHeaders headers = new HttpHeaders(MultiValueMap.fromSingleValue(Map.of(
                    HttpHeaders.COOKIE, "refresh_token=%s; Path=/".formatted(rt.getToken())
            )));

            ResponseEntity<String> res = testRestTemplate.exchange(
                    url(REFRESH_TOKEN_URL),
                    HttpMethod.POST,
                    new HttpEntity<Void>(headers),
                    String.class
            );

            assertThat(res.getStatusCode())
                    .as("Status code should be 401 unauthorized")
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @Nested
    class LogoutTests {

        @Test
        void logout_whenLoggedIn_deletesDbRow_andClearsCookies() {
            ResponseEntity<String> loginRes = login("admin", "admin");
            String deviceId = Objects.requireNonNull(
                    cookieValue("device_id", cookies(loginRes)),
                    "device_id cookie should not be null"
            ).getValue();

            ResponseEntity<String> logoutRes = testRestTemplate.postForEntity(url(LOGOUT_API_URL), null, String.class);

            assertThat(logoutRes.getStatusCode())
                    .as("Status code should be 204 no content")
                    .isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(logoutRes.getBody())
                    .as("Response body should be empty")
                    .isNull();
            assertThat(refreshTokenRepository.findByDeviceIdAndUser_Username(deviceId, "admin"))
                    .as("Refresh token should have been deleted")
                    .isEmpty();

            assertThat(cookieValue("refresh_token", cookies(logoutRes)))
                    .as("Refresh token should have been cleared")
                    .isNotNull()
                    .extracting(HttpCookie::getMaxAge)
                    .isEqualTo(0L);
        }

        @Test
        void logout_whenNotLoggedIn_stillReturnsEmptyBody() {
            ResponseEntity<String> res = testRestTemplate.postForEntity(url(LOGOUT_API_URL), null, String.class);
            assertThat(res.getStatusCode())
                    .isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(res.getBody())
                    .isNull();
        }
    }

    @Nested
    class AuthorizationTests {

        @Test
        void withoutToken_returns401() {
            ResponseEntity<String> res = testRestTemplate.getForEntity(url(PROTECTED_URL), String.class);

            assertThat(res.getStatusCode())
                    .as("Request without token should return 401")
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        void withInvalidToken_returns401() {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth("this.is.not.valid");

            ResponseEntity<String> res = testRestTemplate.exchange(
                    url(PROTECTED_URL),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            assertThat(res.getStatusCode())
                    .as("Invalid token should return 401")
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        void withValidToken_returns200() {
            ResponseEntity<String> loginRes = login("admin", "admin");
            String accessToken = JsonPath.compile("$.access_token").read(loginRes.getBody());
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            ResponseEntity<String> res = testRestTemplate.exchange(
                    url(PROTECTED_URL),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            assertThat(res.getStatusCode())
                    .as("Valid token should access protected endpoint")
                    .isEqualTo(HttpStatus.OK);
        }

        @Test
        void withWrongRole_returns403() {
            User u = new User();
            u.setUsername("bob");
            u.setPassword(passwordEncoder.encode("bob"));
            u.setRole("user");
            userRepository.save(u);
            ResponseEntity<String> loginRes = login("bob", "bob");
            String accessToken = JsonPath.compile("$.access_token").read(loginRes.getBody());

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            ResponseEntity<String> res = testRestTemplate.exchange(
                    url(ADMIN_URL),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            assertThat(res.getStatusCode())
                    .as("Wrong role should return 403")
                    .isEqualTo(HttpStatus.FORBIDDEN);
        }
    }

    @Nested
    class SessionPersistenceTests {

        @Test
        void loginTwice_sameBrowser_shouldKeepSameDeviceId_andRotateRefreshToken() {
            // First login (same browser, same TestRestTemplate instance)
            ResponseEntity<String> first = login("admin", "admin");
            List<HttpCookie> cookies1 = cookies(first);
            HttpCookie rt1 = cookieValue("refresh_token", cookies1);
            HttpCookie device1 = cookieValue("device_id", cookies1);

            assertThat(device1)
                    .as("device_id cookie should exist after first login")
                    .isNotNull();
            assertThat(refreshTokenRepository.findByDeviceIdAndUser_Username(device1.getValue(), "admin"))
                    .as("First DB refresh session should exist")
                    .isPresent();

            // ---- Second login (same browser → cookies automatically sent) ----
            ResponseEntity<String> second = login("admin", "admin");
            List<HttpCookie> cookies2 = cookies(second);
            HttpCookie rt2 = cookieValue("refresh_token", cookies2);
            HttpCookie device2 = cookieValue("device_id", cookies2);

            assertThat(rt2).isNotNull();
            assertThat(device2).isNotNull();
            assertThat(rt2.getValue())
                    .as("Refresh token must rotate on second login (same device)")
                    .isNotEqualTo(rt1.getValue());
            assertThat(device2.getValue())
                    .as("Device ID should NOT change for same browser")
                    .isEqualTo(device1.getValue());

            long count = refreshTokenRepository.countByUser_Username("admin");
            assertThat(count)
                    .as("Only one active session per device/browser")
                    .isEqualTo(1);
        }

        @Test
        void loginFromDifferentDevices_createsTwoSessions() {
            // Device A
            ResponseEntity<String> resA = login("admin", "admin");
            String rtA = Objects.requireNonNull(
                    cookieValue("refresh_token", cookies(resA)),
                    "refresh_token cookie should not be null"
            ).getValue();
            RefreshToken sessionA = refreshTokenRepository.findByToken(rtA).orElseThrow();

            // Device B — new TestRestTemplate with no cookies
            TestRestTemplate otherDevice = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);
            otherDevice.getRestTemplate().getInterceptors().add(AuthControllerTest::contentTypeInterceptor);
            String body = "{\"username\":\"admin\", \"password\":\"admin\"}";
            ResponseEntity<String> resB = otherDevice.postForEntity(url(LOGIN_API_URL), body, String.class);

            String rtB = Objects.requireNonNull(
                    cookieValue("refresh_token", cookies(resB)),
                    "refresh_token cookie should not be null"
            ).getValue();
            RefreshToken sessionB = refreshTokenRepository.findByToken(rtB).orElseThrow();

            assertThat(sessionA.getDeviceId())
                    .as("Device A and B must have different device IDs")
                    .isNotEqualTo(sessionB.getDeviceId());

            long count = refreshTokenRepository.countByUser_Username("admin");
            assertThat(count)
                    .as("Two devices = two sessions")
                    .isEqualTo(2);
        }

        @Test
        void refreshWithWrongDevice_shouldReturn401() {
            // Login to create a valid session
            ResponseEntity<String> loginRes = login("admin", "admin");
            String refreshToken = Objects.requireNonNull(
                    cookieValue("refresh_token", cookies(loginRes)),
                    "refresh_token cookie should not be null"
            ).getValue();

            // Now simulate a refresh WITHOUT sending the device_id cookie
            HttpHeaders wrongDeviceHeaders = new HttpHeaders();
            wrongDeviceHeaders.add(HttpHeaders.COOKIE, "refresh_token=%s".formatted(refreshToken));

            ResponseEntity<String> res = testRestTemplate.exchange(
                    url(REFRESH_TOKEN_URL),
                    HttpMethod.POST,
                    new HttpEntity<>(wrongDeviceHeaders),
                    String.class
            );

            assertThat(res.getStatusCode())
                    .as("Refresh without correct device_id must fail")
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

}
