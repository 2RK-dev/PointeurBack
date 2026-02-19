package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.ApiKeyCreateRequest;
import io.github.two_rk_dev.pointeurback.dto.ApiKeyResponse;
import io.github.two_rk_dev.pointeurback.dto.ApiKeyWithRawToken;
import io.github.two_rk_dev.pointeurback.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyWithRawToken> createApiKey(@Valid @RequestBody ApiKeyCreateRequest requestBody) {
        return ResponseEntity.ok(apiKeyService.create(requestBody));
    }

    @GetMapping
    public ResponseEntity<ApiKeyResponse> getAll() {
        return ResponseEntity.ok(apiKeyService.getAll());
    }

    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<Void> deleteApiKey(@PathVariable Long apiKeyId) {
        apiKeyService.delete(apiKeyId);
        return ResponseEntity.noContent().build();
    }
}
