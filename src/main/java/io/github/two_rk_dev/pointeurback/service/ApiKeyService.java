package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.config.AuthProperties;
import io.github.two_rk_dev.pointeurback.dto.ApiKeyCreateRequest;
import io.github.two_rk_dev.pointeurback.dto.ApiKeyResponse;
import io.github.two_rk_dev.pointeurback.dto.ApiKeyWithRawToken;
import io.github.two_rk_dev.pointeurback.mapper.ApiKeyMapper;
import io.github.two_rk_dev.pointeurback.model.ApiKey;
import io.github.two_rk_dev.pointeurback.repository.ApiKeyRepository;
import io.github.two_rk_dev.pointeurback.service.implementation.ApiKeyHasher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyHasher apiKeyHasher;
    private final ApiKeyMapper apiKeyMapper;
    private final AuthProperties authProperties;

    public UserDetails getUserFromApiKey(String apiKey) {
        byte[] hash = apiKeyHasher.hash(apiKey);
        return apiKeyRepository.getByApiKeyHash(hash)
                .map(k -> new User(k.getName(), k.getName(), List.of()))
                .orElse(null);
    }

    public ApiKeyWithRawToken create(@Valid @RequestBody ApiKeyCreateRequest requestBody) {
        ApiKey apiKey = apiKeyMapper.fromCreateRequest(requestBody);
        String keyPrefix = authProperties.apiKey().keyPrefix();
        String raw = keyPrefix + UUID.randomUUID();
        apiKey.setApiKeyHash(apiKeyHasher.hash(raw));
        ApiKey saved = apiKeyRepository.save(apiKey);
        return apiKeyMapper.toResponseWithRawToken(saved, raw, keyPrefix);
    }

    public List<ApiKeyResponse> getAll() {
        return apiKeyRepository.findAll().stream()
                .map(entity -> apiKeyMapper.toResponse(entity, authProperties.apiKey().keyPrefix()))
                .toList();
    }

    public void delete(Long apiKeyId) {
        apiKeyRepository.deleteById(apiKeyId);
    }
}
