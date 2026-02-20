package io.github.two_rk_dev.pointeurback.service.implementation;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class ApiKeyHasher {
    public byte[] hash(@NotNull String rawToken) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(rawToken.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
