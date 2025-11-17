package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.config.AuthProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final AuthProperties authProperties;

    public UserDetails getUserFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return User.builder()
                .username(claims.getSubject())
                .password("")
                .roles(claims.get("role", String.class))
                .build();
    }

    public String encodeToToken(io.github.two_rk_dev.pointeurback.model.@NotNull User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().toUpperCase())
                .signWith(getSigningKey())
                .expiration(Date.from(OffsetDateTime.now().plusSeconds(authProperties.jwt().expiration()).toInstant()))
                .compact();
    }

    @Contract(pure = true)
    private @NotNull SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(authProperties.jwt().secret().getBytes(StandardCharsets.UTF_8));
    }
}
