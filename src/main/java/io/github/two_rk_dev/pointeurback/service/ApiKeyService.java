package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.dto.ApiKeyCreateRequest;
import io.github.two_rk_dev.pointeurback.dto.ApiKeyResponse;
import io.github.two_rk_dev.pointeurback.dto.ApiKeyWithRawToken;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ApiKeyService {

    public UserDetails getUserFromApiKey(String apiKey) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ApiKeyWithRawToken create(@Valid @RequestBody ApiKeyCreateRequest requestBody) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ApiKeyResponse getAll() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void delete(Long apiKeyId) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
