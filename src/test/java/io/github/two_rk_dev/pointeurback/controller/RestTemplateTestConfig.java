package io.github.two_rk_dev.pointeurback.controller;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.IOException;

@TestConfiguration
class RestTemplateTestConfig {
    public static @NotNull ClientHttpResponse jsonContentTypeInterceptor(
            @NotNull HttpRequest request,
            byte[] body,
            @NotNull ClientHttpRequestExecution execution) throws IOException {

        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return execution.execute(request, body);
    }

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
                ).interceptors(RestTemplateTestConfig::jsonContentTypeInterceptor)
        );
    }
}
