package io.github.two_rk_dev.pointeurback.controller;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public abstract class AbstractRandomPortSpringBootTest {

    public static @NotNull HttpEntity<String> createHttpEntity(String requestBody, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(requestBody, headers);
    }

    @Contract(pure = true)
    @NotNull String url(String path) {
        return "http://localhost:" + this.getPort() + "/api/v1" + path;
    }

    protected abstract int getPort();
}
